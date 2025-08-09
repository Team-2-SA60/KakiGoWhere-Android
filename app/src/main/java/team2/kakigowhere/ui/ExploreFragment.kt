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
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.PlaceDTO
import team2.kakigowhere.databinding.FragmentExploreBinding

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaceAdapter
    private var currentFilteredPlaces = listOf<PlaceDTO>()
    private var originalPlaces = listOf<PlaceDTO>()

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
            originalPlaces = resp.body() ?: emptyList<PlaceDTO>()

            currentFilteredPlaces = originalPlaces

            withContext(Dispatchers.Main) {
                setupRecycler(currentFilteredPlaces.sortedBy { it.name })
                binding.loadingOverlay.visibility = View.GONE
            }
        }

        binding.sortDefault.setOnClickListener {
            currentFilteredPlaces = currentFilteredPlaces.sortedBy { it.name }
            setupRecycler(currentFilteredPlaces)
        }
        binding.sortRating.setOnClickListener {
            currentFilteredPlaces = currentFilteredPlaces.sortedByDescending { it.averageRating }
            setupRecycler(currentFilteredPlaces)
        }
        binding.searchButton.setOnClickListener {
            val q = binding.searchInput.text.toString().trim()
            if (q.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a search term", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentFilteredPlaces = originalPlaces.filter { it.name.contains(q, true) }
            if (currentFilteredPlaces.isEmpty()) {
                setupRecycler(emptyList())
                binding.tvNoResult.visibility = View.VISIBLE
            } else {
                setupRecycler(currentFilteredPlaces.sortedBy { it.name })
            }
        }
        binding.refreshButton.setOnClickListener {
            binding.searchInput.text?.clear()
            binding.tvNoResult.visibility = View.GONE
            currentFilteredPlaces = originalPlaces
            setupRecycler(currentFilteredPlaces.sortedBy { it.name })
        }
    }

    private fun setupRecycler(list: List<PlaceDTO>) {
        adapter = PlaceAdapter(list) { rowItem ->
            // Navigate passing the PlaceDTO directly
            val action = ExploreFragmentDirections
                .actionExploreFragmentToDetailFragment(rowItem.id)
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