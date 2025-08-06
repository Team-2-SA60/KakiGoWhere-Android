package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.ItineraryDTO
import java.time.LocalDate

class SavedFragment : Fragment(), View.OnClickListener {

    private lateinit var touristEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // mock email (TODO: use actual email from user)
        touristEmail = "cy@kaki.com"

        var itineraryList = mutableListOf<ItineraryDTO>()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getItineraries(touristEmail)
                if (response.isSuccessful && response.body() != null) {
                    itineraryList = response.body()!!.toMutableList()
                    Log.d("API PRINT", itineraryList.toString())
                }

                val recyclerView = view.findViewById<RecyclerView>(R.id.itinerary_list)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                val sortedList = itineraryList.sortedBy { LocalDate.parse(it.startDate) }
                recyclerView.adapter = ItineraryAdapter(this@SavedFragment, sortedList) { item ->
                    launchSavedItemFragment(item.id)
                }
            } catch (e: Exception) {
                Log.d("API Error", "Error fetching itineraries")
                Log.d("API Error", e.toString())
            }
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.create_itinerary)
        fab.setOnClickListener(this)
    }

    private fun launchSavedItemFragment(itineraryId: Long) {
        findNavController().navigate(
            SavedFragmentDirections.actionSavedFragmentToSavedItemFragment(itineraryId)
        )
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.create_itinerary -> {
                findNavController().navigate(
                    SavedFragmentDirections.actionSavedFragmentToCreateItineraryFragment(touristEmail)
                )
            }
        }
    }
}