package team2.kakigowhere

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.File

class PlaceAdapter(private var places: List<PlaceSuggestion>) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPlace: ImageView = itemView.findViewById(R.id.imgPlace)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place_suggestion, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]

        holder.tvName.text = place.name
        holder.tvRating.text = "${place.rating} " + "★".repeat(place.rating.toInt()) +
                if (place.rating % 1 >= 0.5) "½" else ""
        holder.tvCategory.text = "Category: ${place.category}"

        val context = holder.itemView.context
        val fileName = place.name.replace(" ", "_").lowercase() + ".jpg"
        val imageFile = getImageFile(context, fileName)

        if (imageFile.exists()) {
            holder.imgPlace.setImageURI(imageFile.toUri())
        } else {
            // Set placeholder first
            holder.imgPlace.setImageResource(R.drawable.placeholder_image)

            // Start coroutine to download image
            CoroutineScope(Dispatchers.Main).launch {
                val downloadedFile = downloadImageToFile(context, place.imageUrl, fileName)
                if (downloadedFile != null && downloadedFile.exists()) {
                    holder.imgPlace.setImageURI(downloadedFile.toUri())
                } else {
                    holder.imgPlace.setImageResource(R.drawable.error_image)
                }
            }
        }
    }

    override fun getItemCount(): Int = places.size
}
