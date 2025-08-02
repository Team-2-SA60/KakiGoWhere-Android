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
import team2.kakigowhere.data.model.Itinerary
import team2.kakigowhere.data.model.ItineraryDTO

class ItineraryAdapter(
    private val context: SavedFragment,
    private val itineraryList: List<ItineraryDTO>,
    private val onItemClick: (Itinerary) -> Unit
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
        val placeImagePath = ApiConstants.IMAGE_URL + itineraryItem.placeDisplayId.toString()

        Glide.with(context)
            .load(placeImagePath)
            .placeholder(R.drawable.kakigowhere)
            .into(holder.image)

        holder.title.text = itineraryItem.title
        holder.dates.text = itineraryItem.startDate
    }

    override fun getItemCount(): Int = itineraryList.size
}