package team2.kakigowhere.ui.map

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import team2.kakigowhere.R
import team2.kakigowhere.data.model.LocationViewModel
import team2.kakigowhere.data.model.PlaceDetailDTO
import team2.kakigowhere.data.model.PlaceViewModel

class MapsFragment : Fragment(), OnMapReadyCallback {

    private val placeViewModel: PlaceViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()

    private lateinit var places: List<PlaceDetailDTO>
    private lateinit var googleMap: GoogleMap
    private lateinit var locationHelper: LocationHelper
    lateinit var locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest>

    private val markersMap = mutableMapOf<Long, Marker>()
    private val args: MapsFragmentArgs by navArgs()
    private var isMapReady = false
    var userHasInteracted = false

    // permission launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> if (isGranted) handleLocation() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSettingsLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                locationViewModel.userDeniedLocation = true
                Toast.makeText(
                    requireContext(),
                    "Enable location to view places near you",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_maps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        places = placeViewModel.places.value!!
        locationHelper = LocationHelper(this, locationViewModel)

        // update map when Place live data is updated
        placeViewModel.places.observe(viewLifecycleOwner) { places ->
            if (places != null) {
                // notify when map is ready
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        isMapReady = true
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        googleMap.setOnCameraMoveStartedListener { reason ->
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                userHasInteracted = true
            }
        }

        val singapore = LatLng(1.290270, 103.851959)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(singapore, 16f))

        // check if location permissions and settings enabled
        if (args.placeId == 0L) {
            if (locationHelper.hasPermission()) handleLocation()
            else locationHelper.requestPermission(locationPermissionLauncher)
        }

        // initialise map of places
        places = placeViewModel.places.value!!
        if (places.isNotEmpty()) {
            addPlaceMarkers(googleMap)
            setLaunchDetailFragment(googleMap)

            // below logics run if navigated from Detail Fragment
            if (args.placeId != 0L) {
                val place = places.find { it.id == args.placeId }!!
                val location = LatLng(place.latitude, place.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                markersMap[args.placeId]?.showInfoWindow()
            }

            val backButton = requireView().findViewById<ImageButton>(R.id.backButton)
            if (args.showBack) {
                backButton.visibility = View.VISIBLE
                backButton.setOnClickListener { findNavController().navigateUp() }
            } else backButton.visibility = View.GONE
        }
    }

    private fun handleLocation() {
        locationHelper.checkLocationSettings(
            onEnabled = { locationHelper.centerToCurrentLocation(googleMap) },
            onFallback = { Toast.makeText(
                requireContext(),
                "Enable location to view places near you",
                Toast.LENGTH_SHORT).show() }
        )
    }

    private fun addPlaceMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            val location = LatLng(place.latitude, place.longitude)
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(place.name))
            marker?.tag = place.id
            markersMap[place.id] = marker!!
        }

        // set custom info window adapter
        googleMap.setInfoWindowAdapter(InfoWindowAdapter(requireContext(), places))

        // handle marker clicks
        googleMap.setOnMarkerClickListener { marker ->
            val place = places.find { it.id == marker.tag }
            if (place != null) {
                val location = LatLng(place.latitude, place.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                marker.showInfoWindow()
                true
            } else {
                false
            }
        }
    }

    private fun setLaunchDetailFragment(googleMap: GoogleMap) {
        googleMap.setOnInfoWindowClickListener { marker ->
            val place = places.find { it.id == marker.tag }
            findNavController().navigate(
                MapsFragmentDirections.actionMapFragmentToDetailFragment(place!!.id)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (isMapReady && locationHelper.hasPermission()) handleLocation()
    }
}