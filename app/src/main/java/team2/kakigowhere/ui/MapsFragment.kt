package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.PlaceDTO

class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private var places: List<PlaceDTO>? = null

    private val callback = OnMapReadyCallback { map ->
        googleMap = map

        // initial zoom to Singapore
        val singapore = LatLng(1.290270, 103.851959)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(singapore, 16f))
        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get notified when the map is ready to be used
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // call list of places from backend api
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getPlaces()
                if (response.isSuccessful && response.body() != null) {
                    places = response.body()!!
                    Log.d("API PRINT", "success")
                    Log.d("API PRINT", places.toString())

                    addPlaceMarkers(googleMap)
                }
            } catch (e: Exception) {
                Log.d("API Error", "Error fetching from API")
                Log.d("API Error", e.toString())
            }
        }
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
        googleMap.setOnMarkerClickListener { marker ->
            val place = places!!.find { it.id == marker.tag }
            if (place != null) {
                val location = LatLng(place.latitude, place.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 18f))
                marker.showInfoWindow()
                true
            } else {
                false // falls back on default behaviour
            }
        }

        // TODO: map.setOnInfoWindowClickListener
    }
}