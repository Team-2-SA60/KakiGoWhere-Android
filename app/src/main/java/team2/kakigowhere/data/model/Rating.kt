package team2.kakigowhere.data.model

data class Rating(
    var ratingId: Long,
    var rating: Int,
    var comment: String,
    var placeId: Long,
    var touristId: Long

)
