package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.FakePlacesData
import team2.kakigowhere.PlaceAdapter
import team2.kakigowhere.PlaceRowItem
import team2.kakigowhere.R
import kotlin.random.Random

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val greeting = view.findViewById<TextView>(R.id.tvGreeting)
        greeting.text = "Hi, Adrian!"

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSuggestions)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val realPlaces = FakePlacesData.getPlaces()

        val placeRowItems = realPlaces.map {
            PlaceRowItem(it, Random.nextDouble(3.5, 5.0))
        }

        recycler.adapter = PlaceAdapter(placeRowItems) { place ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(place)
            findNavController().navigate(action)
        }


    }
}