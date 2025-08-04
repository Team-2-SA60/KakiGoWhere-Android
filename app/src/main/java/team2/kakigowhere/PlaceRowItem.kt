package team2.kakigowhere

import team2.kakigowhere.data.model.Place

data class PlaceRowItem(
    val place: Place,
    val rating: Double = 4.0
) {
    fun imageUrl(): String {
        val base = team2.kakigowhere.data.api.ApiConstants.IMAGE_URL.trimEnd('/')
        return "$base/${place.imagePath}"
    }
}
