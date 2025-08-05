package team2.kakigowhere

import team2.kakigowhere.data.model.PlaceDTO

data class PlaceRowItem(
    val place: PlaceDTO,
    val rating: Double
) {
    fun imageUrl(): String {
        val base = team2.kakigowhere.data.api.ApiConstants.IMAGE_URL.trimEnd('/')
        return "$base/${place.id}"
    }
}
