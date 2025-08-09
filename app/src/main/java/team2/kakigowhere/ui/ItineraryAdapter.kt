package team2.kakigowhere.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.model.ItineraryDTO

class ItineraryAdapter(
    private val context: SavedFragment,
    private val itineraryList: List<ItineraryDTO>,
    private val onItemClick: (ItineraryDTO) -> Unit
) : RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder>() {

    inner class ItineraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById<ImageView>(R.id.itinerary_image)
        val title: TextView = itemView.findViewById<TextView>(R.id.itinerary_title)
        val dates: TextView = itemView.findViewById<TextView>(R.id.itinerary_dates)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItineraryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itinerary_card, parent, false)
        return ItineraryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItineraryViewHolder,
        position: Int
    ) {
        val itineraryItem = itineraryList[position]
        holder.itemView.setOnClickListener { onItemClick(itineraryItem) }

        // if itinerary does not have items
        if (itineraryItem.days == 0L) {
            holder.image.setImageResource(R.drawable.placeholder_image)
            holder.title.text = itineraryItem.title
            holder.dates.text = itineraryItem.startDate
            return
        }

        val placeImagePath = ApiConstants.IMAGE_URL + itineraryItem.placeDisplayId.toString()

        Glide.with(context)
            .load(placeImagePath)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.image)

        holder.title.text = itineraryItem.title
        holder.dates.text = itineraryItem.startDate + " ~ " + itineraryItem.getLastDate().toString() + " · " + itineraryItem.days.toString() + " days"
        //holder.itemView.setOnClickListener { onItemClick(itineraryItem) }
    }

    override fun getItemCount(): Int = itineraryList.size
}