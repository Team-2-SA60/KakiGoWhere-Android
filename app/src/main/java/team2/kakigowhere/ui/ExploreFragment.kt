package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import team2.kakigowhere.PlaceAdapter
import team2.kakigowhere.PlaceRowItem
import team2.kakigowhere.databinding.FragmentExploreBinding
import team2.kakigowhere.FakePlacesData
import team2.kakigowhere.data.api.RetrofitClient

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaceAdapter
    private var originalPlaces: List<PlaceRowItem> = listOf()
    private var currentFilteredPlaces: List<PlaceRowItem> = listOf()

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

        // api to get places
        binding.loadingOverlay.visibility = View.VISIBLE
        lifecycleScope.launch {
            val response = RetrofitClient.api.getPlaces()
            val basicPlaces = response.body() ?: listOf()

            originalPlaces = basicPlaces.map { place ->
            val detail = withContext(Dispatchers.IO) {
                RetrofitClient.api.getPlaceDetail(place.id).body()
            }
            PlaceRowItem(
                id = place.id,
                name = place.name,
                rating = detail?.averageRating ?: 0.0
            )
            }

            //original view
            currentFilteredPlaces = originalPlaces
            adapter = PlaceAdapter(currentFilteredPlaces.sortedBy { it.name })
            binding.recyclerViewExplore.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewExplore.adapter = adapter
            binding.loadingOverlay.visibility = View.GONE
        }

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
                    it.name.contains(query, ignoreCase = true)
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