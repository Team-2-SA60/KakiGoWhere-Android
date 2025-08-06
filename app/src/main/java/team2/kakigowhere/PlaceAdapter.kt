package team2.kakigowhere

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.model.PlaceDTO
import team2.kakigowhere.databinding.PlaceItemBinding

class PlaceAdapter(
    private val places: List<PlaceDTO>,
    private val onItemClick: (PlaceDTO) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(private val binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: PlaceDTO) {
            binding.apply{
                placeName.text = place.name
                placeRating.text = if (place.averageRating == 0.0) "Rating Not Available" else "Rating: %.1f".format(place.averageRating)

                val imagePath = ApiConstants.IMAGE_URL + place.id
                Glide.with(placeImage.context)
                    .load(imagePath)
                    .placeholder(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(placeImage)

                root.setOnClickListener {
                    onItemClick(place)
                }
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