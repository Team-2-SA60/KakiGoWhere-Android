package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.ItineraryDetail
import team2.kakigowhere.data.model.ItineraryDetailDTO
import java.time.LocalDate
import java.util.SortedMap

class ItineraryDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_itinerary_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var itemList = mutableListOf<ItineraryDetailDTO>()
        var itinerary = ItineraryDetailFragmentArgs.fromBundle(requireArguments()).itinerary

        view.findViewById<TextView>(R.id.title_display).text = itinerary.title
        var addDay = view.findViewById<Button>(R.id.add_day)
        addDay.setOnClickListener {
            val addedDate = itinerary.getLastDate().plusDays(1)
            val addedDay = ItineraryDetail(date = addedDate.toString())

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.addItineraryDay(itinerary.id, addedDay)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Added day to itinerary", Toast.LENGTH_LONG)
                        //TODO: how to refresh fragment view after adding day
                    }
                } catch (e: Exception) {
                    Log.d("API Error", e.printStackTrace().toString())
                }
            }
        }

        // set up display of itinerary items
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getItineraryDetails(itinerary.id)
                if (response.isSuccessful && response.body() != null) {
                    itemList = response.body()!!.toMutableList()
                    var sortedListByDate = listToMap(itemList)

                    val recyclerView = view.findViewById<RecyclerView>(R.id.itinerary_days)
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())

                    val adapter = ItineraryDayAdapter(this@ItineraryDetailFragment, sortedListByDate)
                    recyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                Log.d("API Error", "Error fetching itinerary details")
                Log.d("API Error", e.toString())
            }
        }

        // set up view if itinerary has no items
        //TODO
    }

    private fun listToMap(list: List<ItineraryDetailDTO>): SortedMap<LocalDate, List<ItineraryDetailDTO>> {
        var sortedListByOrder = list.sortedBy { it.sequentialOrder }
        val sortedMapByDate = sortedListByOrder.groupBy{ it.itemDate }.toSortedMap()
        return sortedMapByDate
    }

}