package team2.kakigowhere.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import team2.kakigowhere.data.model.LocationViewModel

class LocationHelper(
    private val fragment: MapsFragment,
    private val locationViewModel: LocationViewModel
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
                        if (locationViewModel.userDeniedLocation) return@addOnFailureListener
                        // shows dialog box to prompt user to enable location
                        val intentSenderRequest = IntentSenderRequest.Builder(e.resolution).build()
                        fragment.locationSettingsLauncher.launch(intentSenderRequest)
                    } catch (ex: Exception) {
                        onFallback()
                    }
                } else {
                    onFallback()
                }
            }
    }

    // specify centering to current location if location enabled

    @SuppressLint("MissingPermission")
    fun centerToCurrentLocation(googleMap: GoogleMap) {
        googleMap.isMyLocationEnabled = true
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    if (fragment.userHasInteracted) return
                    var location = result.lastLocation!!
                    var latlng = LatLng(location.latitude, location.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16f))
                }
            },
            Looper.getMainLooper()
        )
    }

}