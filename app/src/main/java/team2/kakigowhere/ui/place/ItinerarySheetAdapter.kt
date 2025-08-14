package team2.kakigowhere.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.ItineraryDTO
import team2.kakigowhere.data.model.ItineraryDetail
import team2.kakigowhere.ui.place.DetailFragment

class ItinerarySheetAdapter(
    private val context: DetailFragment,
    private val placeId: Long,
    private val itineraryList: List<ItineraryDTO>,
    private val onItineraryUpdate: () -> Unit,
) : RecyclerView.Adapter<ItinerarySheetAdapter.ItinerarySheetViewHolder>() {
    inner class ItinerarySheetViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.image)
        val title = itemView.findViewById<TextView>(R.id.title)
        val container = itemView.findViewById<LinearLayout>(R.id.container)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ItinerarySheetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.itinerary_view, parent, false)
        return ItinerarySheetViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItinerarySheetViewHolder,
        position: Int,
    ) {
        val itinerary = itineraryList[position]

        val imagePath = ApiConstants.IMAGE_URL + itinerary.placeDisplayId
        Glide
            .with(context)
            .load(imagePath)
            .signature(ObjectKey(System.currentTimeMillis() / 60000))
            .placeholder(R.drawable.placeholder_image)
            .centerCrop()
            .into(holder.image)

        holder.title.text = itinerary.title + " · " + itinerary.startDate

        // display based on number of days in itinerary
        if (itinerary.days > 0L) {
            for (dayNumber in 1..itinerary.days) {
                val add =
                    Button(context.requireContext()).apply {
                        // set layout parameters for the button
                        text = "Day $dayNumber"
                        textSize = 10f
                        setTextColor("#6200EA".toColorInt())
                        layoutParams =
                            LinearLayout.LayoutParams(120, 80).apply {
                                setMargins(10, 0, 10, 0)
                            }
                        setPadding(0, 0, 0, 0)
                        background = ContextCompat.getDrawable(context, R.drawable.btn_transparent_positive)
                        backgroundTintList = null
                        transformationMethod = null

                        // save item to itinerary on that day
                        setOnClickListener {
                            onDayClick(itinerary, dayNumber)
                        }
                    }
                holder.container.addView(add)
            }
        } else {
            val text =
                TextView(context.requireContext()).apply {
                    text = "No days set for itinerary."
                    layoutParams =
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                        )
                }
            holder.container.addView(text)
        }
    }

    private fun onDayClick(
        itinerary: ItineraryDTO,
        day: Long,
    ) {
        var addDate = itinerary.getStartDate.plusDays(day - 1)
        val detail = ItineraryDetail(date = addDate.toString())
        context.lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.addItemToItinerary(itinerary.id, placeId, detail)
                if (response.isSuccessful) {
                    context.bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    Toast.makeText(context.requireContext(), "Saved to itinerary!", Toast.LENGTH_LONG).show()
                    onItineraryUpdate()
                }
            } catch (e: Exception) {
                Log.d("API Error", e.printStackTrace().toString())
            }
        }
    }

    override fun getItemCount(): Int = itineraryList.size
}
