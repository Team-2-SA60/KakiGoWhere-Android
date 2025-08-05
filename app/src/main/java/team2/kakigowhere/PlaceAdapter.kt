package team2.kakigowhere

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import team2.kakigowhere.databinding.PlaceItemBinding

class PlaceAdapter(
    private val items: List<PlaceRowItem>,
    private val onItemClick: (PlaceRowItem) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(private val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlaceRowItem) {
            // Display name and rating
            binding.placeName.text = item.place.name
            binding.placeRating.text = if (item.rating == 0.0) "Rating Not Available" else "Rating: %.1f".format(item.rating)

            // Load image with Glide
            Glide.with(binding.placeImage.context)
                .load(item.imageUrl())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(binding.placeImage)

            // Click callback passes the full PlaceRowItem
            binding.root.setOnClickListener {
                onItemClick(item)
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
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}