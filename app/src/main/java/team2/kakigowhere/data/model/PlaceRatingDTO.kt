package team2.kakigowhere.data.model



data class PlaceRatingDTO (
    val id: Long,
    val googleId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val averageRating: Double,
    val interestCategories: List<InterestCategoryDTO>,
    val open: Boolean,
    val active: Boolean
)

data class InterestCategoryDTO(
    val id: Long,
    val name: String,
    val description: String
)
