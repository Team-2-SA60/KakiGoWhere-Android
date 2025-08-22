package team2.kakigowhere.ui.place

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.ItineraryViewModel
import team2.kakigowhere.data.model.PlaceDetailDTO
import team2.kakigowhere.databinding.FragmentDetailBinding
import team2.kakigowhere.ui.ItinerarySheetAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale.US

class DetailFragment : Fragment() {
    private lateinit var placeDetail: PlaceDetailDTO
    lateinit var bottomSheet: LinearLayout
    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val itineraryViewModel: ItineraryViewModel by activityViewModels()
    private val args: DetailFragmentArgs by navArgs()

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // handle place detail display
        lifecycleScope.launch {
            val response = RetrofitClient.api.getPlaceDetail(args.placeId)
            if (response.isSuccessful) {
                placeDetail = response.body()!!

                // handle bottom sheet display for adding to itinerary
                setUpBottomSheet()

                binding.apply {
                    // set place details
                    placeName.text = placeDetail.name
                    placeRating.text =
                        if (placeDetail.averageRating == 0.0) {
                            "Rating Not Available"
                        } else {
                            formatRating(placeDetail.averageRating)
                        }
                    placeHours.text = if (placeDetail.open) "Open Now" else "Closed"
                    placeDescription.text = placeDetail.description
                    placeWebsite.text = placeDetail.url
                    renderOpeningHours(placeDetail.openingDescription)

                    val imagePath = ApiConstants.IMAGE_URL + placeDetail.id
                    Glide
                        .with(this@DetailFragment)
                        .load(imagePath)
                        .signature(ObjectKey(System.currentTimeMillis() / 60000))
                        .placeholder(R.drawable.placeholder_image)
                        .centerCrop()
                        .into(placeImage)

                    // set up buttons and clickable text
                    backButton.setOnClickListener {
                        findNavController().navigateUp()
                    }

                    ratingsPage.setOnClickListener {
                        findNavController().navigate(
                            DetailFragmentDirections.actionDetailFragmentToRatingFragment(
                                placeId = placeDetail.id,
                                placeTitle = placeDetail.name,
                            ),
                        )
                    }

                    placeWebsite.setOnClickListener {
                        val url = placeDetail.url
                        if (url.isNotBlank()) {
                            findNavController().navigate(
                                DetailFragmentDirections.actionDetailFragmentToWebViewFragment(url = url),
                            )
                        }
                    }

                    btnShowOnMap.setOnClickListener {
                        findNavController().navigate(
                            DetailFragmentDirections.actionDetailFragmentToMapFragment(
                                placeId = placeDetail.id,
                                showBack = true,
                            ),
                        )
                    }

                    btnAddToItinerary.setOnClickListener {
                        bottomSheet.visibility = View.VISIBLE
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }

                binding.eventContainer.visibility = View.GONE
                val today = LocalDate.now()
                // filter for active today
                val items =
                    placeDetail.placeEvents
                        .filter { e ->
                            runCatching {
                                val s = LocalDate.parse(e.startDate)
                                val d = LocalDate.parse(e.endDate)
                                !today.isBefore(s) && !today.isAfter(d)
                            }.getOrDefault(false)
                        }.distinctBy { Triple(it.name, it.startDate, it.endDate) }
                        .sortedWith(compareBy({ it.startDate }, { it.endDate }, { it.name }))

                if (items.isNotEmpty()) {
                    val lines =
                        items.map { e ->
                            val desc = e.description.takeIf { it.isNotBlank() }?.let { " ($it)" } ?: ""
                            "${dateRangeFormatter(e.startDate, e.endDate)}: ${e.name}$desc"
                        }
                    binding.eventDescription.text = lines.joinToString("\n")
                    binding.eventContainer.visibility = View.VISIBLE
                } else {
                    binding.eventContainer.visibility = View.GONE
                }
            }
        }
    }

    private fun setUpBottomSheet() {
        bottomSheet = requireView().findViewById<LinearLayout>(R.id.itinerary_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        itineraryViewModel.itineraries.observe(viewLifecycleOwner) { itineraries ->
            val itineraryList = itineraries.sortedBy { LocalDate.parse(it.startDate) }
            val recyclerView = bottomSheet.findViewById<RecyclerView>(R.id.itinerary_view)

            val emptySheet = bottomSheet.findViewById<TextView>(R.id.empty_sheet)
            if (itineraryList.isEmpty()) {
                emptySheet.visibility = View.VISIBLE
            } else {
                emptySheet.visibility = View.GONE
            }

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter =
                ItinerarySheetAdapter(
                    this@DetailFragment,
                    placeDetail.id,
                    itineraryList,
                    onItineraryUpdate = {
                        val prefs = requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
                        val email = prefs.getString("user_email", "") ?: ""
                        itineraryViewModel.loadItineraries(email)
                    },
                )
        }

        bottomSheet.findViewById<Button>(R.id.close_sheet).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun renderOpeningHours(raw: String?) {
        binding.openingHoursContainer.removeAllViews()
        if (raw.isNullOrBlank()) return

        val lines = raw.split('\n')
        for (line in lines) {
            val t = line.trim()
            if (t.isEmpty()) continue
            val tv = TextView(requireContext())
            tv.text = t
            tv.textSize = 10f
            binding.openingHoursContainer.addView(tv)
        }
    }

    private fun formatRating(v: Double): String {
        val i = v.toInt()
        return if (v == i.toDouble()) "$i / 5" else String.format(US, "%.1f / 5", v)
    }

    // pretty format the date
    private val dateFormat = DateTimeFormatter.ofPattern("d MMM yyyy", US)

    private fun dateRangeFormatter(
        start: String,
        end: String,
    ): String =
        try {
            val s = LocalDate.parse(start)
            val e = LocalDate.parse(end)
            if (s == e) s.format(dateFormat) else "${s.format(dateFormat)} – ${e.format(dateFormat)}"
        } catch (_: Exception) {
            "$start – $end"
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
