package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.FakePlacesData
import team2.kakigowhere.PlaceAdapter
import team2.kakigowhere.PlaceSuggestion
import team2.kakigowhere.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val greeting = view.findViewById<TextView>(R.id.tvGreeting)
        greeting.text = "Hi, Adrian!" // or dynamic

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSuggestions)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Convert Place to PlaceSuggestion (used by the adapter)
        val suggestions = FakePlacesData.getPlaces().map { place ->
            PlaceSuggestion(
                name = place.name,
                rating = 4.0, // You can change this if rating is available
                category = "placeholder", // Add real category if needed
                imageUrl = place.imagePath // Use imageUrl here!
            )
        }

        recycler.adapter = PlaceAdapter(suggestions)
    }
}
