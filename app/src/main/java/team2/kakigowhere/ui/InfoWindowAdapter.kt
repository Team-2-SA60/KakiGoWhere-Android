package team2.kakigowhere.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.model.Place

class InfoWindowAdapter(
    private val context: Context,
    private val places: List<Place>
) : GoogleMap.InfoWindowAdapter {
    private val window: View = LayoutInflater.from(context).inflate(R.layout.info_window, null)

    override fun getInfoWindow(marker: Marker): View {
        val place = places.find { it.id == marker.tag }

        val imagePath = ApiConstants.IMAGE_URL + place!!.imagePath
        val imageView = window.findViewById<ImageView>(R.id.icon)
        Glide.with(context)
            .load(imagePath)
            .into(imageView)

        window.findViewById<TextView>(R.id.title).text = place.name
        window.findViewById<TextView>(R.id.snippet).text = place.description

        return window
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }
}