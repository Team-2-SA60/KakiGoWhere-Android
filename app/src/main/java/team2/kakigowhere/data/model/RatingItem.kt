package team2.kakigowhere.data.model

data class RatingItem (
    val id: Long,
    val touristId: Long,
    val touristName: String,
    val rating: Int,
    val comment: String?
)