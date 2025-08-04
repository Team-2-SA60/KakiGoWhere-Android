package team2.kakigowhere.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.data.model.RatingItem
import team2.kakigowhere.R

class RatingsAdapter(private var items: List<RatingItem>) :
    RecyclerView.Adapter<RatingsAdapter.RatingViewHolder>() {

    fun submitList(newList: List<RatingItem>) {
        items = newList
        notifyDataSetChanged()
    }

    inner class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvReviewerName)
        val rating: TextView = itemView.findViewById(R.id.tvReviewerRating)
        val comment: TextView = itemView.findViewById(R.id.tvReviewerComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rating_row, parent, false)
        return RatingViewHolder(v)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val dto = items[position]
        holder.name.text = dto.touristName
        holder.rating.text = "${dto.rating} / 5"
        holder.comment.text = dto.comment ?: ""
    }

    override fun getItemCount(): Int = items.size
}