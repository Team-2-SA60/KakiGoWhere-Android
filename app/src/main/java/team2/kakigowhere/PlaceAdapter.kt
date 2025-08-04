package team2.kakigowhere

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import team2.kakigowhere.databinding.PlaceItemBinding
import team2.kakigowhere.data.model.Place

class PlaceAdapter(
    private val places: List<PlaceRowItem>,
    private val onItemClick: (Place) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(private val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rowItem: PlaceRowItem) {
            val place = rowItem.place

            // Name & rating
            binding.placeName.text = place.name
            binding.placeRating.text = "Rating: %.1f".format(rowItem.rating)

            // Load image with Glide
            Glide.with(binding.placeImage.context)
                .load(place.imagePath)
                .placeholder(R.drawable.placeholder_image) // your placeholder
                .error(R.drawable.error_image)             // your error drawable
                .centerCrop()
                .into(binding.placeImage)

            // Click callback
            binding.root.setOnClickListener {
                onItemClick(place)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = PlaceItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount(): Int = places.size
}
