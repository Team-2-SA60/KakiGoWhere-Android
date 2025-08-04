package team2.kakigowhere.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import team2.kakigowhere.databinding.FragmentDetailBinding
import team2.kakigowhere.downloadImageToFile
import kotlin.math.round
import kotlin.random.Random

class DetailFragment : Fragment() {

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
        val place = args.place

        // Random rating
        val raw = Random.nextDouble(1.0, 5.0)
        val randomRating = round(raw * 10) / 10.0

        binding.placeName.text = place.name
        binding.placeRating.text = "%.1f / 5".format(randomRating)
        binding.placeHours.text =
            "Opening Hours: ${place.openingHour} - ${place.closingHour}"
        binding.placeDescription.text = place.description
        binding.placeWebsite.text = place.url

        binding.placeWebsite.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(place.url)))
        }

        // Load image
        lifecycleScope.launch {
            val imageFile = downloadImageToFile(
                requireContext(),
                place.imagePath,
                "place_${place.id}.jpg"
            )
            imageFile?.let {
                val bmp = BitmapFactory.decodeFile(it.absolutePath)
                binding.placeImage.setImageBitmap(bmp)
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnShowOnMap.setOnClickListener {
            // Pass Float args
            val action = DetailFragmentDirections
                .actionDetailFragmentToMapFragment(
                    lat = place.latitude.toFloat(),
                    lng = place.longitude.toFloat()
                )
            findNavController().navigate(action)
        }

        binding.btnShowOnMap.setOnClickListener {
            val action = DetailFragmentDirections
                .actionDetailFragmentToMapFragment(
                    lat = place.latitude.toFloat(),
                    lng = place.longitude.toFloat(),
                    showBack = true
                )
            findNavController().navigate(action)
        }

        binding.btnBookmark.setOnClickListener { /* TODO */ }
        binding.btnAddToItinerary.setOnClickListener { /* TODO */ }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
