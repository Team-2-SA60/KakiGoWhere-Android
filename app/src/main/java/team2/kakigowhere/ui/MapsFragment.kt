package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.PlacesRepository
import team2.kakigowhere.data.model.Place

class MapsFragment : Fragment(), OnMapReadyCallback {

    private val repository = PlacesRepository()
    private var places: List<Place> = emptyList()
    private var mapReady = false
    private var googleMap: GoogleMap? = null
    private val args: MapsFragmentArgs by navArgs()

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

        // Kick off fetching places
        lifecycleScope.launch {
            try {
                places = repository.fetchPlaces()
                // If map is already ready, add markers now
                if (mapReady) addMarkersAndCenter()
            } catch (e: Exception) {
                Log.e("MapsFragment", "Error fetching places", e)
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
}
