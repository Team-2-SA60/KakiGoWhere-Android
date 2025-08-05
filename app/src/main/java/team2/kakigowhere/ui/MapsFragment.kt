package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.model.PlaceDTO
import team2.kakigowhere.data.model.PlaceViewModel

class MapsFragment : Fragment(), OnMapReadyCallback {

    val placeViewModel: PlaceViewModel by activityViewModels()

    private lateinit var places: List<PlaceDTO>
    private var mapReady = false
    private var googleMap: GoogleMap? = null
    private val args: MapsFragmentArgs by navArgs()

//    private val callback = OnMapReadyCallback { googleMap ->
//        // initial zoom to Singapore
//        val singapore = LatLng(1.290270, 103.851959)
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 14f))
//        googleMap.uiSettings.isZoomControlsEnabled = true
//
//        // set up markers for places on map
//        if (places != null) {
//            addPlaceMarkers(googleMap)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_maps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button
        view.findViewById<Button>(R.id.backButton).apply {
            visibility = if (args.showBack) View.VISIBLE else GONE
            setOnClickListener { findNavController().navigateUp() }
        }

        // call list of places from backend api
        lifecycleScope.launch {
            try {
                places = placeViewModel.places.value!!
                // If map is already ready, add markers now
                if (mapReady) addMarkersAndCenter()
            } catch (e: Exception) {
                Log.e("MapsFragment", "Error fetching places", e)

//
//                val response = RetrofitClient.api.getPlaces()
//                if (response.isSuccessful && response.body() != null) {
//                    places = response.body()!!
//                    Log.d("API PRINT", "success")
//                    Log.d("API PRINT", places.toString())
//
//                    addPlaceMarkers(googleMap)
//                }
//            } catch (e: Exception) {
//                Log.d("API Error", "Error fetching from API")
//                Log.d("API Error", e.toString())
            }
        }

        // Initialize the GoogleMap asynchronously
        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
            .getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        mapReady = true
        map.uiSettings.isZoomControlsEnabled = true
        // If data has already loaded, add markers now
        if (places.isNotEmpty()) {
            addMarkersAndCenter()
        }
    }
    private fun addMarkersAndCenter() {
        val map = googleMap ?: return
        // Add a marker for each place
        places.forEach { p ->
            val pos = LatLng(p.latitude, p.longitude)
            map.addMarker(MarkerOptions().position(pos).title(p.name))
        }
        // Center on the coordinates passed in
        val target = LatLng(args.lat.toDouble(), args.lng.toDouble())
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 14f))
    }

    private fun addPlaceMarkers(googleMap: GoogleMap) {
        places!!.forEach { place ->
            val location = LatLng(place.latitude, place.longitude)
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(place.name))
            marker?.tag = place.id
        }

        // set custom info window adapter
        googleMap.setInfoWindowAdapter(InfoWindowAdapter(requireContext(), places!!))

        // handle marker clicks

//        googleMap.setOnMarkerClickListener { marker ->
//            val place = places!!.find { it.id == marker.tag }
//            if (place != null) {
//                val location = LatLng(place.latitude, place.longitude)
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
//                marker.showInfoWindow()
//                true
//            } else {
//                false // falls back on default behaviour
//            }
//        }

        // TODO: map.setOnInfoWindowClickListener
    }
}