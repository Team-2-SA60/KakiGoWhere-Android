package team2.kakigowhere.ui.itinerary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.ItineraryDTO
import team2.kakigowhere.data.model.ItineraryDetail
import team2.kakigowhere.data.model.ItineraryDetailDTO
import team2.kakigowhere.data.model.ItineraryViewModel
import java.time.LocalDate
import java.util.SortedMap

class ItineraryDetailFragment : Fragment(), View.OnClickListener {

    private val itineraryViewModel: ItineraryViewModel by activityViewModels()
    private lateinit var touristItinerary: ItineraryDTO
    private lateinit var itemList: MutableList<ItineraryDetailDTO>
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_itinerary_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        email = prefs.getString("user_email", "") ?: ""

        touristItinerary = ItineraryDetailFragmentArgs.fromBundle(requireArguments()).itinerary
        view.findViewById<TextView>(R.id.title_display).text = touristItinerary.title

        var deleteBtn = view.findViewById<Button>(R.id.delete_itinerary)
        var addDayBtn = view.findViewById<Button>(R.id.add_day)
        var deleteDayBtn = view.findViewById<Button>(R.id.delete_day)
        var backBtn = view.findViewById<ImageButton>(R.id.detail_back)

        deleteBtn.setOnClickListener(this)
        addDayBtn.setOnClickListener(this)
        deleteDayBtn.setOnClickListener(this)
        backBtn.setOnClickListener(this)

        // set up display of itinerary items
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getItineraryDetails(touristItinerary.id)
                if (response.isSuccessful && response.body() != null) {
                    itemList = response.body()!!.toMutableList()

                    // if empty list of itinerary
                    val emptyText = view.findViewById<TextView>(R.id.empty_itinerary)
                    if (itemList.isEmpty()) emptyText.visibility = View.VISIBLE
                    else emptyText.visibility = View.GONE

                    // render items if itinerary list not empty
                    var sortedMapByDate = listToMap(itemList)
                    val recyclerView = view.findViewById<RecyclerView>(R.id.itinerary_days)
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())

                    val adapter = ItineraryDayAdapter(this@ItineraryDetailFragment, sortedMapByDate)
                    recyclerView.adapter = adapter

                    // disable delete day button if no days
                    deleteDayBtn.isEnabled = itemList.isNotEmpty()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error displaying itinerary", Toast.LENGTH_SHORT).show()
                Log.d("API Error", e.toString())
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.delete_itinerary -> {
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.api.deleteItinerary(touristItinerary.id)
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Deleted itinerary", Toast.LENGTH_LONG).show()
                            itineraryViewModel.loadItineraries(email)
                            findNavController().navigateUp()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error deleting itinerary", Toast.LENGTH_SHORT).show()
                        Log.d("API Error", e.toString())
                    }
                }
            }
            R.id.delete_day -> {
                var sortedListByDate = itemList.sortedBy { it.itemDate }
                var lastDate = sortedListByDate.last().date

                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.api.deleteItineraryDay(touristItinerary.id, lastDate)
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Deleted day from itinerary", Toast.LENGTH_LONG).show()
                            itineraryViewModel.loadItineraries(email)
                            findNavController().navigateUp()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error deleting day from itinerary", Toast.LENGTH_SHORT).show()
                        Log.d("API Error", e.toString())
                    }
                }
            }
            R.id.add_day -> {
                val addedDate = touristItinerary.getLastDate().plusDays(1)
                val addedDay = ItineraryDetail(date = addedDate.toString())

                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.api.addItineraryDay(touristItinerary.id, addedDay)
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Added day to itinerary", Toast.LENGTH_LONG).show()
                            itineraryViewModel.loadItineraries(email)
                            findNavController().navigateUp()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error adding day to itinerary", Toast.LENGTH_SHORT).show()
                        Log.d("API Error", e.toString())
                    }
                }
            }
            R.id.detail_back -> {
                findNavController().navigateUp()
            }
        }
    }

    // helper method to sort list of itinerary items into map by days

    private fun listToMap(list: List<ItineraryDetailDTO>): SortedMap<LocalDate, List<ItineraryDetailDTO>> {
        var sortedListByOrder = list.sortedBy { it.sequentialOrder }
        val sortedMapByDate = sortedListByOrder.groupBy { it.itemDate }.toSortedMap()
        return sortedMapByDate
    }
}