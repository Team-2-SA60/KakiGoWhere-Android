package team2.kakigowhere.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.collection.LruCache
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.model.PlaceDetailDTO

/**
 * Faster InfoWindow that caches bitmaps and forces redraw when async loads finish.
 * - Uses getInfoContents() (recommended) and returns null for getInfoWindow().
 * - Downscales to view size to avoid decoding huge images.
 * - LruCache keeps a few already-opened images hot.
 */

class InfoWindowAdapter(
    private val context: Context,
    private val places: List<PlaceDetailDTO>,
) : GoogleMap.InfoWindowAdapter {

    // Reuse a single view for performance
    private val window: View = LayoutInflater.from(context).inflate(R.layout.info_window, null)
    private val iv: ImageView = window.findViewById(R.id.icon)
    private val tvTitle: TextView = window.findViewById(R.id.title)
    private val tvSnippet: TextView = window.findViewById(R.id.snippet)

    // Tiny in-memory cache for bitmaps keyed by placeId
    private val bitmapCache = object : LruCache<Long, Bitmap>(50) {}

    override fun getInfoWindow(marker: Marker): View? = null

    override fun getInfoContents(marker: Marker): View {
        val place = places.find { it.id == marker.tag } ?: return window

        tvTitle.text = place.name
        tvSnippet.text = place.address

        // Try cache first
        val cached = bitmapCache.get(place.id)
        if (cached != null) {
            iv.setImageBitmap(cached)
            return window
        }

        // Lightweight placeholder shown immediately
        iv.setImageResource(R.drawable.placeholder_image)

        // Build URL and load as bitmap so we can cache + control size
        val imagePath = ApiConstants.IMAGE_URL + place.id
        Glide.with(context)
            .asBitmap()
            .load(imagePath)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            // Match info_window.xml ImageView size to avoid huge decodes
            .override(100, 80)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmapCache.put(place.id, resource)
                    // Because info windows are static bitmaps, force a redraw
                    if (marker.isInfoWindowShown) marker.showInfoWindow()
                }
                override fun onLoadCleared(placeholder: Drawable?) { /* no-op */ }
            })

        return window
    }
}