package team2.kakigowhere.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.R
import team2.kakigowhere.data.model.RatingItem

class RatingsAdapter : ListAdapter<RatingItem, RatingsAdapter.RatingViewHolder>(DIFF) {
    companion object {
        private val DIFF =
            object : DiffUtil.ItemCallback<RatingItem>() {
                override fun areItemsTheSame(oldItem: RatingItem, newItem: RatingItem): Boolean =
                    oldItem.ratingId == newItem.ratingId

                override fun areContentsTheSame(oldItem: RatingItem, newItem: RatingItem): Boolean =
                    oldItem == newItem
            }
    }

    inner class RatingViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvReviewerName)
        val rating: TextView = itemView.findViewById(R.id.tvReviewerRating)
        val comment: TextView = itemView.findViewById(R.id.tvReviewerComment)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RatingViewHolder {
        val v =
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_rating_row, parent, false)
        return RatingViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: RatingViewHolder,
        position: Int,
    ) {
        val dto = getItem(position)
        holder.name.text = dto.touristName
        holder.rating.text = "${dto.rating} / 5"
        holder.comment.text = dto.comment ?: ""
    }
}
