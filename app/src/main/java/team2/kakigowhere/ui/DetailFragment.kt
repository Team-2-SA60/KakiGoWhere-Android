package team2.kakigowhere.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.PlaceDetailDTO
import team2.kakigowhere.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private lateinit var placeDetail: PlaceDetailDTO

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val response = RetrofitClient.api.getPlaceDetail(args.placeId)
            if (response.isSuccessful) {
                placeDetail = response.body()!!
            }
        }

        // Populate text fields
        binding.placeName.text = placeDetail.name
        binding.placeRating.text = if (placeDetail.averageRating == 0.0) "Rating Not Available" else placeDetail.averageRating.toString()
        binding.placeHours.text = if (placeDetail.isOpen) "Open Now" else "Closed"
        binding.placeDescription.text = "PlaceHolder" // TODO:
        binding.placeWebsite.text = "https://www.google.com/maps/search/?api=1&query=${Uri.encode(placeDetail.name)}"

        // Clickable website
        binding.placeWebsite.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(placeDetail.name)}")))
        }
        var imageUrl = ApiConstants.IMAGE_URL + placeDetail.id

        // Load image with Glide (lifecycle-aware)
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .centerCrop()
            .into(binding.placeImage)

        // Back button
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Show on Map (passing coords + showBack=true)
        binding.btnShowOnMap.setOnClickListener {
            val action = DetailFragmentDirections
                .actionDetailFragmentToMapFragment(
//                    lat = place.latitude.toFloat(),
//                    lng = place.longitude.toFloat(),
                    placeId = placeDetail.id,
                    showBack = true
                )
            findNavController().navigate(action)
        }

        // Stubs for future logic
        binding.btnBookmark.setOnClickListener { /* TODO: bookmark */ }
        binding.btnAddToItinerary.setOnClickListener { /* TODO: add to itinerary */ }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
