package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.R
import team2.kakigowhere.data.model.Itinerary
import java.time.LocalDate

class SavedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var itineraryList = mutableListOf<Itinerary>(
            Itinerary(1, "My awesome itinerary", LocalDate.of(2025, 8, 1)),
            Itinerary(2, "My good itinerary", LocalDate.of(2025, 8, 7)),
            Itinerary(3, "My bad itinerary", LocalDate.of(2025, 8, 12))
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.itinerary_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ItineraryAdapter(itineraryList) { item ->
            //  openItineraryDetail()
        }

        recyclerView.adapter = adapter
    }

}