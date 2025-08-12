package team2.kakigowhere.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

class LocationHelper(
    private val fragment: MapsFragment,
    private val defaultLocation: LatLng = LatLng(1.290270, 103.851959),
    private val defaultZoom: Float = 16f
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(fragment.requireActivity())
    private val settingsClient = LocationServices.getSettingsClient(fragment.requireActivity())
    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
        .setMinUpdateIntervalMillis(2000)
        .build()

    // to check if permission is granted, else request it

    fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(launcher: ActivityResultLauncher<String>) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // to check if location is enabled, else request it

    fun checkLocationSettings(
        onEnabled: () -> Unit,
        onFallback: () -> Unit
    ) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        settingsClient.checkLocationSettings(builder.build())
            .addOnSuccessListener { onEnabled() }
            .addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        // shows dialog box to prompt user to enable location
                        e.startResolutionForResult(fragment.requireActivity(), 1001)
                    } catch (ex: Exception) {
                        onFallback()
                    }
                } else {
                    onFallback()
                }
            }
    }

    // specify centering to current location or default location

    @SuppressLint("MissingPermission")
    fun centerToCurrentLocation(googleMap: GoogleMap) {
        if (!hasPermission()) return

        googleMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, defaultZoom))
                } ?: run {
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { freshLocation ->
                            freshLocation?.let {
                                val userLocation = LatLng(freshLocation.latitude, freshLocation.longitude)
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, defaultZoom))
                            } ?: run {
                                centerToDefaultLocation(googleMap)
                            }
                        }
                        .addOnFailureListener {
                            centerToDefaultLocation(googleMap)
                        }
                }
            }
            .addOnFailureListener {
                centerToDefaultLocation(googleMap)
            }
    }

    fun centerToDefaultLocation(googleMap: GoogleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoom))
        Toast.makeText(fragment.requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show()
    }

}