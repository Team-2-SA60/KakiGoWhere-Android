package team2.kakigowhere.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.Place

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var places: List<Place> = emptyList()
    private lateinit var googleMap: GoogleMap
    private val args: MapsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_maps, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button: only show if showBack==true
        view.findViewById<Button>(R.id.backButton).apply {
            visibility = if (args.showBack) VISIBLE else GONE
            setOnClickListener { findNavController().navigateUp() }
        }

        // Initialize the map
        (childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment)
            .getMapAsync(this)

        // Fetch places from API
        lifecycleScope.launch {
            try {
                val resp = RetrofitClient.api.getPlaces()
                if (resp.isSuccessful) {
                    places = resp.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("MapsFragment", "Error fetching places", e)
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        // Add markers for all places
        places.forEach { p ->
            val pos = LatLng(p.latitude, p.longitude)
            googleMap.addMarker(MarkerOptions().position(pos).title(p.name))
        }

        // Center on passed-in coordinates
        val target = LatLng(args.lat.toDouble(), args.lng.toDouble())
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 14f))
    }
}
