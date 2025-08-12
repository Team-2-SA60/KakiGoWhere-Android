package team2.kakigowhere.ui

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

    // --- Helpers / placeholder for empty days ---
    private fun isEmptyDay(list: List<ItineraryDetailDTO>): Boolean {
        return list.isEmpty() || list.all { it.placeId == 0L || it.placeTitle.isBlank() }
    }

    private inner class PlaceholderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(android.R.id.text1)
    }

    private inner class PlaceholderAdapter(private val message: String) :
        RecyclerView.Adapter<PlaceholderViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceholderViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return PlaceholderViewHolder(v)
        }

        override fun getItemCount(): Int = 1

        override fun onBindViewHolder(holder: PlaceholderViewHolder, position: Int) {
            holder.text.text = message
            holder.text.textAlignment = View.TEXT_ALIGNMENT_CENTER
            holder.text.setPadding(
                holder.text.paddingLeft,
                holder.text.paddingTop + 16,
                holder.text.paddingRight,
                holder.text.paddingBottom + 16
            )
            holder.text.setTextColor(android.graphics.Color.GRAY) // Greyed-out hint style
            holder.text.setTypeface(null, android.graphics.Typeface.ITALIC)
        }
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

        // Use the date directly from the key instead of indexing into possibly empty dayItems
        holder.day.text = "Day ${position + 1} · ${dateKey}"

        holder.itemsRv.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemsRv.isNestedScrollingEnabled = false

        if (isEmptyDay(dayItems)) {
            holder.itemsRv.adapter = PlaceholderAdapter("No items added yet")
        } else {
            holder.itemsRv.adapter = ItineraryItemAdapter(context, dayItems)
        }
    }

    override fun getItemCount(): Int = items.size
}