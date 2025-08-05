package team2.kakigowhere

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlaceAdapter(private var places: List<PlaceRowItem>) :
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    fun submitList(newList: List<PlaceRowItem>) {
        if (places.map { it.id } == newList.map { it.id }) return
        places = newList
        notifyDataSetChanged()
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPlace: ImageView = itemView.findViewById(R.id.imgPlace)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place_suggestion, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]

        holder.tvName.text = place.name

        // Round to 1 decimal place
        val roundedRating = "%.1f".format(place.rating.coerceAtLeast(0.0))
        // Stars based on integer part only
        val numberOfStars = place.rating.toInt()
        val stars = "★".repeat(numberOfStars)
        holder.tvRating.text = "$roundedRating $stars"

        Log.d("PlaceAdapter", "Loading image for id=${place.id}, url=${place.imageUrl()}")

        Glide.with(holder.imgPlace.context)
            .load(place.imageUrl())
            .into(holder.imgPlace)
    }

    override fun getItemCount(): Int = places.size
}
