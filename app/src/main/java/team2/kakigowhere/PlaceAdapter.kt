package team2.kakigowhere

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

            binding.placeName.text = place.name
            binding.placeRating.text = "Rating: %.1f".format(rowItem.rating)

            CoroutineScope(Dispatchers.Main).launch {
                val imageFile = downloadImageToFile(
                    binding.root.context,
                    place.imagePath,
                    "place_${place.id}.jpg"
                )

                imageFile?.let { file ->
                    binding.placeImage.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                }
            }

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
