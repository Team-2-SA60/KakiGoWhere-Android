package team2.kakigowhere.ui.itinerary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.R
import team2.kakigowhere.data.model.ItineraryDetailDTO
import java.time.LocalDate
import java.util.SortedMap

class ItineraryDayAdapter(
    private val context: ItineraryDetailFragment,
    private val items: SortedMap<LocalDate, List<ItineraryDetailDTO>>
) : RecyclerView.Adapter<ItineraryDayAdapter.DayViewHolder>() {

    inner class DayViewHolder(dayView: View) : RecyclerView.ViewHolder(dayView) {
        val day: TextView = dayView.findViewById<TextView>(R.id.day)
        val itemsRv: RecyclerView = dayView.findViewById<RecyclerView>(R.id.itinerary_details)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itinerary_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayKeys = items.keys.toList()
        val dateKey = dayKeys[position]
        val dayItems = items[dateKey] ?: emptyList()

        holder.day.text = "Day ${position + 1} · ${dateKey}"

        holder.itemsRv.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemsRv.isNestedScrollingEnabled = false
        holder.itemsRv.adapter = ItineraryItemAdapter(context, dayItems)
    }

    override fun getItemCount(): Int = items.size
}