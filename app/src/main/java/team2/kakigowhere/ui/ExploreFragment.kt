package team2.kakigowhere.ui

import team2.kakigowhere.PlaceAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.FakePlacesData
import team2.kakigowhere.PlaceRowItem
import team2.kakigowhere.R
import team2.kakigowhere.databinding.FragmentExploreBinding
import kotlin.random.Random

class ExploreFragment : Fragment() {

    private lateinit var adapter: PlaceAdapter
    private lateinit var placeRowItems: List<PlaceRowItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_explore, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewExplore)
        val searchInput = view.findViewById<EditText>(R.id.search_input)
        val searchButton = view.findViewById<Button>(R.id.search_button)
        val refreshButton = view.findViewById<Button>(R.id.refresh_button)
        val sortDefault = view.findViewById<Button>(R.id.sort_default)
        val sortRating = view.findViewById<Button>(R.id.sort_rating)
        val noResult = view.findViewById<TextView>(R.id.tv_no_result)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        placeRowItems = FakePlacesData.getPlaces().map {
            PlaceRowItem(it, Random.nextDouble(3.5, 5.0))
        }

        fun updateRecycler(items: List<PlaceRowItem>) {
            noResult.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            adapter = PlaceAdapter(items) { place ->
                val action = ExploreFragmentDirections.actionExploreFragmentToDetailFragment(place)
                findNavController().navigate(action)
            }
            recyclerView.adapter = adapter
        }

        updateRecycler(placeRowItems)

        searchButton.setOnClickListener {
            val query = searchInput.text.toString().trim()
            val filtered = placeRowItems.filter {
                it.place.name.contains(query, ignoreCase = true)
            }
            updateRecycler(filtered)
        }

        refreshButton.setOnClickListener {
            searchInput.setText("")
            updateRecycler(placeRowItems)
        }

        sortDefault.setOnClickListener {
            updateRecycler(placeRowItems)
        }

        sortRating.setOnClickListener {
            val sorted = placeRowItems.sortedByDescending { it.rating }
            updateRecycler(sorted)
        }
    }
}
