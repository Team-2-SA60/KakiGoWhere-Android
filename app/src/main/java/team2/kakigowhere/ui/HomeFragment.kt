package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import team2.kakigowhere.PlaceAdapter
import team2.kakigowhere.R
import team2.kakigowhere.data.model.PlaceViewModel

class HomeFragment : Fragment() {

    private val placeViewModel: PlaceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set greeting
        view.findViewById<TextView>(R.id.tvGreeting).text = "Hi, Adrian!"

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerSuggestions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // observe live data from Place view model
        placeViewModel.places.observe(viewLifecycleOwner) { places ->
            if (places != null) {
                recyclerView.adapter = PlaceAdapter(places) { place ->
                    findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToDetailFragment(place.id)
                    )
                }
            }
        }

        // observe errors from Place view model
        placeViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }
}
