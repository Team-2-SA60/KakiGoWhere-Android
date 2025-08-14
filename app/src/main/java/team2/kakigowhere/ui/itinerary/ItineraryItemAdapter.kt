package team2.kakigowhere.ui.itinerary

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.model.ItineraryDetailDTO

class ItineraryItemAdapter(
    private val context: ItineraryDetailFragment,
    private val items: List<ItineraryDetailDTO>
) : RecyclerView.Adapter<ItineraryItemAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val row: LinearLayout = itemView.findViewById<LinearLayout>(R.id.row_item)
        val image: ImageView = itemView.findViewById<ImageView>(R.id.item_image)
        val title: TextView = itemView.findViewById<TextView>(R.id.item_title)
        val hours: TextView = itemView.findViewById<TextView>(R.id.item_hours)
        val notes: TextView = itemView.findViewById<TextView>(R.id.item_notes)
        val edit: Button = itemView.findViewById<Button>(R.id.edit_item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itinerary_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int,
    ) {
        val item = items[position]

        // if no place, no items are in the list: render "no items" text
        if (item.placeId == 0L) {
            holder.row.removeAllViews()
            holder.row.addView(
                TextView(holder.row.context).apply {
                    text = "No itinerary items on this day."
                    setTextColor(ContextCompat.getColor(context, R.color.unselected_color))
                    layoutParams =
                        LinearLayout
                            .LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                            ).apply {
                                gravity = Gravity.CENTER
                            }
                },
            )
        }

        // else render itinerary items as usual
        else {
            val placeImagePath = ApiConstants.IMAGE_URL + item.placeId.toString()

            Glide
                .with(context)
                .load(placeImagePath)
                .signature(ObjectKey(System.currentTimeMillis() / 60000))
                .placeholder(R.drawable.kakigowhere)
                .centerCrop()
                .into(holder.image)

            holder.title.text = item.placeTitle
            holder.hours.text = if (item.placeIsOpen) "Open · " + item.placeOpenHours else "Closed"
            holder.notes.text = "Notes: " + item.notes

            holder.edit.setOnClickListener {
                context.findNavController().navigate(
                    ItineraryDetailFragmentDirections.actionItineraryItemFragmentToEditItineraryItemFragment(item),
                )
            }

            holder.itemView.setOnClickListener {
                context.findNavController().navigate(
                    ItineraryDetailFragmentDirections.actionItineraryItemFragmentToDetailFragment(item.placeId)
                )
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
