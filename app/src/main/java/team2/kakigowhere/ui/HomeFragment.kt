package team2.kakigowhere.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.PlaceViewModel
import team2.kakigowhere.ui.HomeFragmentDirections

class HomeFragment : Fragment() {

    private val placeViewModel: PlaceViewModel by activityViewModels()

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
        loadUpcomingItinerary(view)

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
    // (Remove any placeholder example loadUpcomingItinerary function)

    private fun loadUpcomingItinerary(root: View) {
        val card = root.findViewById<CardView>(R.id.home_upcoming_card)
        val tvTitle = root.findViewById<TextView>(R.id.itinerary_title)
        val tvDates = root.findViewById<TextView>(R.id.itinerary_dates)

        // Default state while loading
        tvTitle.text = "Fetching upcoming itinerary..."
        tvDates.text = ""
        card.isClickable = false

        val prefs = requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("user_email", null) ?: "cy@kaki.com"

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = RetrofitClient.api.getItineraries(email)
                if (resp.isSuccessful) {
                    val list = resp.body().orEmpty()
                    val today = LocalDate.now()

                    // Map to parsed dates (assumes ISO-8601 yyyy-MM-dd)
                    val parsed = list.mapNotNull { dto ->
                        runCatching { LocalDate.parse(dto.startDate) }
                            .getOrNull()
                            ?.let { date -> dto to date }
                    }

                    // Choose closest future date; if none, choose closest overall
                    val upcoming = parsed
                        .filter { (_, d) -> !d.isBefore(today) }
                        .minByOrNull { (_, d) -> d }
                        ?: parsed.minByOrNull { (_, d) -> kotlin.math.abs(ChronoUnit.DAYS.between(today, d)) }

                    if (upcoming != null) {
                        val (itinerary, start) = upcoming
                        val fmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                        tvTitle.text = "Trip starting ${start.format(fmt)}"
                        tvDates.text = "Tap to view day-by-day plan"

                        card.isClickable = true
                        card.setOnClickListener {
                            val action = HomeFragmentDirections.actionHomeFragmentToSavedItemFragment(itinerary.id)
                            findNavController().navigate(action)
                        }
                    } else {
                        tvTitle.text = "No itineraries yet"
                        tvDates.text = "Create one from the Saved tab"
                        card.isClickable = false
                    }
                } else {
                    tvTitle.text = "Could not load itineraries"
                    tvDates.text = "Try again later"
                    card.isClickable = false
                }
            } catch (e: Exception) {
                tvTitle.text = "Could not load itineraries"
                tvDates.text = "Check your connection"
                card.isClickable = false
            }
        }
    }
}
