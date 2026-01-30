package team2.kakigowhere.ui.place

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.api.MLRetrofitClient
import team2.kakigowhere.data.api.RecommendRequest
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.PlaceViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class HomeFragment : Fragment() {
    private val placeViewModel: PlaceViewModel by activityViewModels()
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve stored user name
        val prefs = requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", "Guest") ?: "Guest"
        view.findViewById<TextView>(R.id.tvGreeting).text = "Hi, $userName"
        loadUpcomingItinerary(view)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerSuggestions)
        val emptyView = view.findViewById<TextView>(R.id.tvEmpty)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PlaceAdapter { place ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(place.id),
            )
        }
        recyclerView.adapter = adapter

        // observe live data from Place view model
        placeViewModel.places.observe(viewLifecycleOwner) { places ->
            if (places == null) return@observe

            viewLifecycleOwner.lifecycleScope.launch {
                // interests from SharedPrefs (what ML expects)
                val prefs = requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
                val interests =
                    prefs
                        .getStringSet("user_interest_names", emptySet())
                        ?.toList()
                        .orEmpty()

                if (interests.isEmpty()) {
                    showEmpty(emptyView, recyclerView)
                    adapter.submitList(emptyList())
                    return@launch
                }

                // load fresh recommendations on entering Home
                val recIds = loadRecIds(interests)

                val byId = places.associateBy { it.id } // build a Map of <Long, PlaceDTO>
                val recPlaces = recIds.mapNotNull { byId[it] } // for each id, look up in the Map

                if (recPlaces.isEmpty()) {
                    showEmpty(emptyView, recyclerView)
                    adapter.submitList(emptyList())
                } else {
                    adapter.submitList(recPlaces)
                    showList(recyclerView, emptyView)
                }
            }
        }

        // observe errors from Place view model
        placeViewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show() }
        }
    }

    private fun showEmpty(
        emptyView: View,
        list: View,
    ) {
        emptyView.visibility = View.VISIBLE
        list.visibility = View.GONE
    }

    private fun showList(
        list: View,
        emptyView: View,
    ) {
        list.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
    }

    // runcatching wraps in try-catch block
    private suspend fun loadRecIds(interests: List<String>): List<Long> =
        runCatching {
            withContext(Dispatchers.IO) {
                MLRetrofitClient.api.getRecommendations(RecommendRequest(interests))
            }.map { it.id }
        }.getOrElse { emptyList() }

    private fun loadUpcomingItinerary(root: View) {
        val card = root.findViewById<CardView>(R.id.home_upcoming_card)
        val tvTitle = root.findViewById<TextView>(R.id.itinerary_title)
        val tvDates = root.findViewById<TextView>(R.id.itinerary_dates)
        val image = root.findViewById<ImageView>(R.id.itinerary_image)

        // Default state while loading
        tvTitle.text = "Fetching upcoming itinerary..."
        tvDates.text = ""
        card.isClickable = false
        image.setImageResource(R.drawable.placeholder_image)

        val prefs = requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("user_email", "") ?: ""

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val resp = RetrofitClient.api.getItineraries(email)
                if (resp.isSuccessful) {
                    val list = resp.body().orEmpty()
                    val today = LocalDate.now()

                    // Map to parsed start + computed end (via ItineraryDTO.getLastDate()).
                    // Ongoing means: today ∈ [startDate, endDate]. Otherwise allow upcoming only (start >= today).
                    val parsed =
                        list.mapNotNull { dto ->
                            val start =
                                runCatching { LocalDate.parse(dto.startDate) }.getOrNull()
                                    ?: return@mapNotNull null
                            val end = runCatching { dto.getLastDate() }.getOrElse { start }
                            Triple(dto, start, end)
                        }

                    // Prefer ongoing (earliest start). If none, choose the soonest upcoming.
                    val ongoingPick =
                        parsed
                            .filter { (_, start, end) -> !today.isBefore(start) && !today.isAfter(end) }
                            .minByOrNull { it.second }
                    val upcomingPick =
                        parsed
                            .filter { (_, start, _) -> !start.isBefore(today) }
                            .minByOrNull { it.second }
                    val upcoming = ongoingPick ?: upcomingPick

                    if (upcoming != null) {
                        val (itinerary, start, end) = upcoming
                        val fmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                        val ongoing = !today.isBefore(start) && !today.isAfter(end)
                        tvTitle.text =
                            if (ongoing) {
                                "Trip in progress — started ${start.format(fmt)}"
                            } else {
                                "Trip starting ${start.format(fmt)}"
                            }

                        tvDates.text = "Tap to view day-by-day plan"

                        // Load itinerary hero image (follow SavedFragment logic)
                        runCatching {
                            val imageUrl = ApiConstants.IMAGE_URL + itinerary.placeDisplayId.toString()
                            Glide
                                .with(this@HomeFragment)
                                .load(imageUrl)
                                .signature(ObjectKey(System.currentTimeMillis() / 60000))
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.placeholder_image)
                                .into(image)
                        }.onFailure {
                            image.setImageResource(R.drawable.placeholder_image)
                        }

                        card.isClickable = true
                        card.setOnClickListener {
                            val action = HomeFragmentDirections.actionHomeFragmentToItineraryItemFragment(itinerary)
                            findNavController().navigate(action)
                        }
                    } else {
                        tvTitle.text = "No Upcoming itineraries"
                        tvDates.text = "Create one from the 'Itinerary' tab"
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
