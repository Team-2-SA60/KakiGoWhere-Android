package team2.kakigowhere.ui.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.model.PlaceDetailDTO
import team2.kakigowhere.databinding.ItemPlaceBinding

class PlaceAdapter(
    private val onItemClick: (PlaceDetailDTO) -> Unit,
) : ListAdapter<PlaceDetailDTO, PlaceAdapter.PlaceViewHolder>(DIFF) {
    companion object {
        private val DIFF =
            object : DiffUtil.ItemCallback<PlaceDetailDTO>() {
                override fun areItemsTheSame(oldItem: PlaceDetailDTO, newItem: PlaceDetailDTO): Boolean =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: PlaceDetailDTO, newItem: PlaceDetailDTO): Boolean =
                    oldItem == newItem
            }
    }

    inner class PlaceViewHolder(
        private val binding: ItemPlaceBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: PlaceDetailDTO) {
            binding.apply {
                placeName.text = place.name
                placeRating.text = if (place.averageRating == 0.0) "Rating Not Available" else "Rating: %.1f".format(place.averageRating)

                val imagePath = ApiConstants.IMAGE_URL + place.id
                Glide
                    .with(placeImage.context)
                    .load(imagePath)
                    .signature(ObjectKey(System.currentTimeMillis() / 60000))
                    .placeholder(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(placeImage)

                root.setOnClickListener { onItemClick(place) }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PlaceViewHolder {
        val binding =
            ItemPlaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PlaceViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }
}
