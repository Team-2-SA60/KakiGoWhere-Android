package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import team2.kakigowhere.PlaceAdapter
import team2.kakigowhere.PlaceSuggestion
import team2.kakigowhere.R
import team2.kakigowhere.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    val binding get() = _binding!!

    private lateinit var adapter: PlaceAdapter
    private var currentFilteredPlaces: List<PlaceSuggestion> = listOf()

    // fake data
    //avoid detekt check
    @Suppress("MagicNumber")
    private val originalPlaces =
        listOf(
            PlaceSuggestion("Marina Bay Sands", 4.2, "Entertainment, Shopping", R.drawable.marina_bay_sands),
            PlaceSuggestion("Singapore Zoo", 4.5, "Wildlife and Zoos", R.drawable.marina_bay_sands),
            PlaceSuggestion("Sentosa", 4.0, "Entertainment", R.drawable.marina_bay_sands),
            PlaceSuggestion("Sands Marina", 4.7, "Shopping", R.drawable.marina_bay_sands),
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // original view
        binding.recyclerViewExplore.layoutManager = LinearLayoutManager(requireContext())
        currentFilteredPlaces = originalPlaces
        adapter = PlaceAdapter(currentFilteredPlaces.sortedBy { it.name })
        binding.recyclerViewExplore.adapter = adapter

        // default filter
        binding.sortDefault.setOnClickListener {
            val sorted = currentFilteredPlaces.sortedBy { it.name }
            adapter = PlaceAdapter(sorted)
            binding.recyclerViewExplore.adapter = adapter
            binding.tvNoResult.visibility = View.GONE
        }

        // rating filter
        binding.sortRating.setOnClickListener {
            val sorted = currentFilteredPlaces.sortedByDescending { it.rating }
            adapter = PlaceAdapter(sorted)
            binding.recyclerViewExplore.adapter = adapter
            binding.tvNoResult.visibility = View.GONE
        }

        // search
        binding.searchButton.setOnClickListener {
            val query =
                binding.searchInput.text
                    .toString()
                    .trim()

            // input nothing
            if (query.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a search term", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // search name or category
            val filtered =
                originalPlaces.filter {
                    it.name.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
                }

            currentFilteredPlaces = filtered

            // no result or have result
            if (filtered.isEmpty()) {
                adapter = PlaceAdapter(emptyList())
                binding.recyclerViewExplore.adapter = adapter
                binding.tvNoResult.visibility = View.VISIBLE
                binding.tvNoResult.text = "No such place"
            } else {
                adapter = PlaceAdapter(filtered.sortedBy { it.name })
                binding.recyclerViewExplore.adapter = adapter
                binding.tvNoResult.visibility = View.GONE
            }
        }

        // Refresh
        binding.refreshButton.setOnClickListener {
            binding.searchInput.text.clear()
            binding.tvNoResult.visibility = View.GONE
            currentFilteredPlaces = originalPlaces
            adapter = PlaceAdapter(currentFilteredPlaces.sortedBy { it.name })
            binding.recyclerViewExplore.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
