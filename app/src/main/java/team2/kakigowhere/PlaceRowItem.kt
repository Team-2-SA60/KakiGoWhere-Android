package team2.kakigowhere

data class PlaceRowItem(
    val id: Long,
    val name: String,
    val rating: Double
) {
    fun imageUrl(): String {
        val base = team2.kakigowhere.data.api.ApiConstants.IMAGE_URL.trimEnd('/')
        return "$base/$id"
    }
}
