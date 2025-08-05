package team2.kakigowhere.data

import android.net.Uri
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.api.ApiConstants
import team2.kakigowhere.data.model.PlaceDTO
import team2.kakigowhere.data.model.Place

class PlacesRepository {
    private val api = RetrofitClient.api

    /**
     * Fetches the raw DTOs from the API, then maps each PlaceDTO
     * into your domain Parcelable Place model.
     */
    suspend fun fetchPlaces(): List<Place> {
        val resp = api.getPlaces()
        if (!resp.isSuccessful) throw Exception("API error ${resp.code()}")
        val dtoList: List<PlaceDTO> = resp.body()!!  // now matches List<PlaceDTO>

        return dtoList.map { dto ->
            Place(
                id          = dto.id,
                name        = dto.name,
                description = dto.address,
                // Use googleId in the image path if that's how your backend serves images
                imagePath   = ApiConstants.IMAGE_URL + dto.id,
                // Provide a map-search URL so users can “open” the location
                url         = "https://www.google.com/maps/search/?api=1&query=${Uri.encode(dto.name)}",
                openingHour = if (dto.open) "Open Now" else "Closed",
                closingHour = "",
                latitude    = dto.latitude,
                longitude   = dto.longitude,
                active      = dto.active
            )
        }
    }
}
