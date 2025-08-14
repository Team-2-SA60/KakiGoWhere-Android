package team2.kakigowhere.ui.itinerary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import team2.kakigowhere.R
import team2.kakigowhere.data.model.ItineraryDTO
import team2.kakigowhere.data.model.ItineraryViewModel
import java.time.LocalDate

class ItineraryFragment : Fragment() {

    private lateinit var email: String
    private val itineraryViewModel: ItineraryViewModel by activityViewModels()
    private val prefsName = "shared_prefs"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_itinerary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        email = prefs.getString("user_email", "") ?: ""
        var itineraryList = listOf<ItineraryDTO>()

        itineraryViewModel.itineraries.observe(viewLifecycleOwner) { itineraries ->
            itineraryList = itineraries

            val emptyText = view.findViewById<TextView>(R.id.empty_itineraries)
            if (itineraries.isEmpty()) emptyText.visibility = View.VISIBLE
            else emptyText.visibility = View.GONE

            val recyclerView = view.findViewById<RecyclerView>(R.id.itinerary_list)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            val sortedList = itineraryList.sortedBy { LocalDate.parse(it.startDate) }
            recyclerView.adapter = ItineraryAdapter(this@ItineraryFragment, sortedList) { item ->
                launchSavedItemFragment(item)
            }
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.create_itinerary)
        fab.setOnClickListener {
            findNavController().navigate(
                ItineraryFragmentDirections.actionItineraryFragmentToCreateItineraryFragment(email)
            )
        }
    }

    private fun launchSavedItemFragment(itinerary: ItineraryDTO) {
        findNavController().navigate(
            ItineraryFragmentDirections.actionItineraryFragmentToItineraryItemFragment(itinerary)
        )
    }
}