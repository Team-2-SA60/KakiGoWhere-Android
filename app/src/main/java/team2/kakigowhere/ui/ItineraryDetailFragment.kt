package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
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
        var id = ItineraryDetailFragmentArgs.fromBundle(requireArguments()).itineraryId;
        Log.d("API PRINT", id.toString())

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getItineraryDetails(id);
                if (response.isSuccessful && response.body() != null) {
                    itemList = response.body()!!.toMutableList()
                    Log.d("API PRINT", itemList.toString())

                    var sortedListByDate = listToMap(itemList)
                    Log.d("API PRINT", sortedListByDate.toString())

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
    }

    private fun listToMap(list: List<ItineraryDetailDTO>): SortedMap<LocalDate, List<ItineraryDetailDTO>> {
        val sortedMapByDate: SortedMap<LocalDate, List<ItineraryDetailDTO>> = list
            .groupBy { it.dateActual }
            .toSortedMap()
        return sortedMapByDate
    }

}