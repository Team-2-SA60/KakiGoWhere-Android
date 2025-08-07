package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class SavedFragment : Fragment(), View.OnClickListener {

    private lateinit var touristEmail: String
    private val itineraryViewModel: ItineraryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: get email from shared prefs
        touristEmail = "cy@kaki.com"

        var itineraryList = listOf<ItineraryDTO>()

        itineraryViewModel.itineraries.observe(viewLifecycleOwner) { itineraries ->
            itineraryList = itineraries

            val recyclerView = view.findViewById<RecyclerView>(R.id.itinerary_list)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            val sortedList = itineraryList.sortedBy { LocalDate.parse(it.startDate) }
            recyclerView.adapter = ItineraryAdapter(this@SavedFragment, sortedList) { item ->
                launchSavedItemFragment(item.id)
            }
        }

        // TODO: able to open empty itinerary
        // TODO: add day to itinerary?

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