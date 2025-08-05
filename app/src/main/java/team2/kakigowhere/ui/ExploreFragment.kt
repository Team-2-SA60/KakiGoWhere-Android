package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import team2.kakigowhere.PlaceAdapter
import team2.kakigowhere.PlaceRowItem
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.PlaceDTO
import team2.kakigowhere.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaceAdapter
    private var originalPlaces = listOf<PlaceRowItem>()
    private var currentFilteredPlaces = listOf<PlaceRowItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loadingOverlay.visibility = View.VISIBLE

        // Fetch PlaceDTOs
        lifecycleScope.launch {
            val resp = RetrofitClient.api.getPlaces()
            val dtoList = resp.body() ?: emptyList<PlaceDTO>()

            originalPlaces = dtoList.map { dto ->
                PlaceRowItem(dto, dto.averageRating)
            }
            currentFilteredPlaces = originalPlaces

            withContext(Dispatchers.Main) {
                setupRecycler(currentFilteredPlaces)
                binding.loadingOverlay.visibility = View.GONE
            }
        }

        binding.sortDefault.setOnClickListener {
            currentFilteredPlaces = currentFilteredPlaces.sortedBy { it.place.name }
            setupRecycler(currentFilteredPlaces)
        }
        binding.sortRating.setOnClickListener {
            currentFilteredPlaces = currentFilteredPlaces.sortedByDescending { it.rating }
            setupRecycler(currentFilteredPlaces)
        }
        binding.searchButton.setOnClickListener {
            val q = binding.searchInput.text.toString().trim()
            if (q.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a search term", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val filtered = originalPlaces.filter { it.place.name.contains(q, true) }
            if (filtered.isEmpty()) {
                setupRecycler(emptyList())
                binding.tvNoResult.apply {
                    text = "No such place"
                    visibility = View.VISIBLE
                }
            } else {
                currentFilteredPlaces = filtered
                setupRecycler(filtered)
            }
        }
        binding.refreshButton.setOnClickListener {
            binding.searchInput.text?.clear()
            binding.tvNoResult.visibility = View.GONE
            currentFilteredPlaces = originalPlaces
            setupRecycler(originalPlaces)
        }
    }

    private fun setupRecycler(list: List<PlaceRowItem>) {
        adapter = PlaceAdapter(list) { rowItem ->
            // Navigate passing the PlaceDTO directly
            val action = ExploreFragmentDirections
                .actionExploreFragmentToDetailFragment(rowItem.place)
            findNavController().navigate(action)
        }

        binding.recyclerViewExplore.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ExploreFragment.adapter
        }
        binding.tvNoResult.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}