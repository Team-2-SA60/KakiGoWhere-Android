package team2.kakigowhere.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.PlaceAdapter
import team2.kakigowhere.PlaceRowItem
import team2.kakigowhere.R
import team2.kakigowhere.data.model.PlaceViewModel

class HomeFragment : Fragment() {

    private val viewModel: PlaceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve stored user name
        val prefs = requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "Guest") ?: "Guest"
        view.findViewById<TextView>(R.id.tvGreeting).text = "Hi, $userName"

        // RecyclerView setup
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerSuggestions)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Observe live data from ViewModel
        viewModel.places.observe(viewLifecycleOwner) { dtoList ->
            val items = dtoList.map { dto ->
                PlaceRowItem(dto, dto.averageRating)
            }
            recycler.adapter = PlaceAdapter(items) { rowItem ->
                val dto = rowItem.place
                val action = HomeFragmentDirections
                    .actionHomeFragmentToDetailFragment(dto)
                findNavController().navigate(action)
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        // Trigger loading from backend
        viewModel.loadPlaces()
    }
}
