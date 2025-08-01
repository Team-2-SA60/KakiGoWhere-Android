package team2.kakigowhere.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.R
import team2.kakigowhere.data.model.Itinerary

class ItineraryAdapter(
    private val itineraryList: List<Itinerary>,
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
        holder.image.setImageResource(R.drawable.kakigowhere)
        holder.title.text = itineraryItem.title
        holder.dates.text = itineraryItem.dateStart.toString()
    }

    override fun getItemCount(): Int = itineraryList.size
}