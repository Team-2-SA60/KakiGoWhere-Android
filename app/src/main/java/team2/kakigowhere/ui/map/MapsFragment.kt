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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import team2.kakigowhere.R
import team2.kakigowhere.data.model.LocationViewModel
import team2.kakigowhere.data.model.PlaceDetailDTO
import team2.kakigowhere.data.model.PlaceViewModel

class MapsFragment :
    Fragment(),
    OnMapReadyCallback {
    private val placeViewModel: PlaceViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()

    private lateinit var places: List<PlaceDetailDTO>
    private lateinit var googleMap: GoogleMap
    private lateinit var locationHelper: LocationHelper
    lateinit var locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest>

    // Marker map no longer needed with cluster manager
    private lateinit var clusterManager: ClusterManager<PlaceClusterItem>
    private lateinit var clusterRenderer: DefaultClusterRenderer<PlaceClusterItem>
    private val args: MapsFragmentArgs by navArgs()
    private var isMapReady = false
    var userHasInteracted = false

    // permission launcher
    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                handleLocation()
            } else {
                Toast
                    .makeText(
                        requireContext(),
                        "Enable location to view places near you",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSettingsLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult(),
            ) { result ->
                if (result.resultCode != Activity.RESULT_OK) {
                    locationViewModel.userDeniedLocation = true
                    Toast
                        .makeText(
                            requireContext(),
                            "Enable location to view places near you",
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.fragment_maps, container, false)

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize helper; places will be loaded via observer when available
        locationHelper = LocationHelper(this, locationViewModel)

        // update map when Place live data is updated
        placeViewModel.places.observe(viewLifecycleOwner) { list ->
            // When places load the first time, ensure map is created
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
            // Store latest list
            if (list != null) places = list
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
            if (locationHelper.hasPermission()) {
                handleLocation()
            } else {
                locationHelper.requestPermission(locationPermissionLauncher)
            }
        }

        // initialise map of places
        val current = if (this::places.isInitialized) places else placeViewModel.places.value.orEmpty()
        if (current.isNotEmpty()) {
            places = current
            setUpClustering()

            // below logics run if navigated from Detail Fragment
            if (args.placeId != 0L) {
                val item = clusterManager.algorithm.items.find { it.place.id == args.placeId }
                if (item != null) {
                    val location = item.position
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                    // Attempt to show info window for the item marker
                    val m = (clusterManager.renderer as? DefaultClusterRenderer<PlaceClusterItem>)?.getMarker(item)
                    m?.showInfoWindow()
                }
            }

            val backButton = requireView().findViewById<ImageButton>(R.id.backButton)
            if (args.showBack) {
                backButton.visibility = View.VISIBLE
                backButton.setOnClickListener { findNavController().navigateUp() }
            } else {
                backButton.visibility = View.GONE
            }
        }
    }

    private fun setUpClustering() {
        // Create or reset cluster manager
        clusterManager = ClusterManager(requireContext(), googleMap)
        clusterRenderer =
            object : DefaultClusterRenderer<PlaceClusterItem>(requireContext(), googleMap, clusterManager) {
                override fun onClusterItemRendered(item: PlaceClusterItem, marker: Marker) {
                    super.onClusterItemRendered(item, marker)
                    // Preserve old behavior: tag marker with place id so InfoWindowAdapter works
                    marker.tag = item.place.id
                }
            }
        clusterManager.renderer = clusterRenderer

        // Forward map events to cluster manager
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)

        // Custom info window for individual items
        clusterManager.markerCollection.setInfoWindowAdapter(InfoWindowAdapter(requireContext(), places))

        // Navigate when user taps info window
        clusterManager.setOnClusterItemInfoWindowClickListener { item ->
            findNavController().navigate(
                MapsFragmentDirections.actionMapFragmentToDetailFragment(item.place.id),
            )
        }

        // Nice UX: center when tapping an item (let default show info window)
        clusterManager.setOnClusterItemClickListener { item ->
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(item.position, 16f))
            false
        }

        // Add all items and cluster
        val items = places.map { PlaceClusterItem(it) }
        clusterManager.clearItems()
        clusterManager.addItems(items)
        clusterManager.cluster()

        // Make clusters clickable: zoom into bounds of items
        clusterManager.setOnClusterClickListener { cluster ->
            try {
                val builder = LatLngBounds.Builder()
                cluster.items.forEach { builder.include(it.position) }
                val bounds = builder.build()
                val padding = 100 // px
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
            } catch (_: Exception) {
                // Fallback: slight zoom-in on cluster position
                val target = cluster.position
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(target, googleMap.cameraPosition.zoom + 1f))
            }
            true
        }
    }

    private fun handleLocation() {
        locationHelper.checkLocationSettings(
            onEnabled = { locationHelper.centerToCurrentLocation(googleMap) },
            onFallback = {
                Toast
                    .makeText(
                        requireContext(),
                        "Enable location to view places near you",
                        Toast.LENGTH_SHORT,
                    ).show()
            },
        )
    }

    // Legacy marker helpers removed in favor of clustering

    override fun onResume() {
        super.onResume()
        if (isMapReady && locationHelper.hasPermission()) handleLocation()
    }
}
