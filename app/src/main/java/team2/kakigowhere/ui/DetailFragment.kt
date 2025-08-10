package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.ItineraryViewModel
import team2.kakigowhere.data.model.PlaceDetailDTO
import team2.kakigowhere.databinding.FragmentDetailBinding
import java.time.LocalDate
import java.util.Locale.*

class DetailFragment : Fragment() {

    private lateinit var placeDetail: PlaceDetailDTO
    lateinit var bottomSheet: LinearLayout
    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val itineraryViewModel: ItineraryViewModel by activityViewModels()
    private val args: DetailFragmentArgs by navArgs()

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                        if (placeDetail.averageRating == 0.0) "Rating Not Available"
                        else
                            formatRating(placeDetail.averageRating)
                    placeHours.text = if (placeDetail.isOpen) "Open Now" else "Closed"
                    placeDescription.text = placeDetail.description
                    placeWebsite.text = placeDetail.URL
                    renderOpeningHours(placeDetail.openingDescription)

                    val imagePath = ApiConstants.IMAGE_URL + placeDetail.id
                    Glide.with(this@DetailFragment)
                        .load(imagePath)
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
                                placeTitle = placeDetail.name
                            )
                        )
                    }

                    placeWebsite.setOnClickListener {
                        val url = placeDetail.URL
                        if (url.isNotBlank()) {
                            findNavController().navigate(
                                DetailFragmentDirections.actionDetailFragmentToWebViewFragment(url = url)
                            )
                        }
                    }

                    btnShowOnMap.setOnClickListener {
                        findNavController().navigate(
                            DetailFragmentDirections.actionDetailFragmentToMapFragment(
                                placeId = placeDetail.id,
                                showBack = true
                            )
                        )
                    }

                    btnAddToItinerary.setOnClickListener {
                        bottomSheet.visibility = View.VISIBLE
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }

                    root.setOnClickListener {
                        // TODO: change button click to be a cross out
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }

                // Stubs for future logic
                // TODO: change bookmark btn to open in google Maps or WebView?
            }
        }

    }

    private fun setUpBottomSheet() {
        bottomSheet = requireView().findViewById<LinearLayout>(R.id.itinerary_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        itineraryViewModel.itineraries.observe(viewLifecycleOwner) { itineraries ->
            val itineraryList = itineraries.sortedBy { LocalDate.parse(it.startDate) }
            val recyclerView = bottomSheet.findViewById<RecyclerView>(R.id.itinerary_view)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = ItinerarySheetAdapter(
                this@DetailFragment,
                placeDetail.id,
                itineraryList,
                onItineraryUpdate = {
                    itineraryViewModel.loadItineraries("cy@kaki.com") // TODO: get shared prefs
                })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

