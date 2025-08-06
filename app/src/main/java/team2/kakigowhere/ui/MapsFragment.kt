package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import team2.kakigowhere.data.model.PlaceDTO
import team2.kakigowhere.data.model.PlaceViewModel

class MapsFragment : Fragment(), OnMapReadyCallback {

    private val placeViewModel: PlaceViewModel by activityViewModels()

    private lateinit var places: List<PlaceDTO>
    private lateinit var googleMap: GoogleMap

    private val markersMap = mutableMapOf<Long, Marker>()
    private val args: MapsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_maps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        places = placeViewModel.places.value!!

        // notify when map is ready
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        val singapore = LatLng(1.290270, 103.851959)

        googleMap = map
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 16f))
        googleMap.uiSettings.isZoomControlsEnabled = true

        if (places.isNotEmpty()) {
            addPlaceMarkers(googleMap)
            if (args.placeId != 0L) {
                val place = places.find { it.id == args.placeId }!!
                val location = LatLng(place.latitude, place.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                markersMap[args.placeId]?.showInfoWindow()
            }

            val backButton = requireView().findViewById<Button>(R.id.backButton)
            if (args.showBack) {
                backButton.visibility = View.VISIBLE
                backButton.setOnClickListener { findNavController().navigateUp() }
            } else backButton.visibility = View.GONE
        }
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

        // TODO: map.setOnInfoWindowClickListener
    }
}