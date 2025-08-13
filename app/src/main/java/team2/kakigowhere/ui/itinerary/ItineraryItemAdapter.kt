package team2.kakigowhere.ui.itinerary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.model.ItineraryDetailDTO

class ItineraryItemAdapter(
    private val context: ItineraryDetailFragment,
    private val items: List<ItineraryDetailDTO>
) : RecyclerView.Adapter<ItineraryItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById<ImageView>(R.id.item_image)
        val title: TextView = itemView.findViewById<TextView>(R.id.item_title)
        val hours: TextView = itemView.findViewById<TextView>(R.id.item_hours)
        val notes: TextView = itemView.findViewById<TextView>(R.id.item_notes)
        val edit: Button = itemView.findViewById<Button>(R.id.edit_item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itinerary_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        val item = items[position]

        if (item.placeId == 0L) {
            holder.itemView.visibility = View.GONE
        } else {
            val placeImagePath = ApiConstants.IMAGE_URL + item.placeId.toString()

            Glide.with(context)
                .load(placeImagePath)
                .placeholder(R.drawable.kakigowhere)
                .centerCrop()
                .into(holder.image)

            holder.title.text = item.placeTitle
            holder.hours.text = if (item.placeIsOpen) "Open · " + item.placeOpenHours else "Closed"
            holder.notes.text = "Notes: " + item.notes

            holder.edit.setOnClickListener {
                context.findNavController().navigate(
                    ItineraryDetailFragmentDirections.actionItineraryItemFragmentToEditItineraryItemFragment(item)
                )
            }
        }
    }

    override fun getItemCount(): Int = items.size
}