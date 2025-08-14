package team2.kakigowhere.data.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rating(
    var ratingId: Long,
    var rating: Int,
    var comment: String,
    var placeId: Long,
    var touristId: Long

) : Parcelable

data class RatingItem (
    val ratingId: Long,
    val touristId: Long,
    val touristName: String,
    val rating: Int,
    val comment: String?
)

data class RatingRequest (
    val rating: Int,
    val comment: String
)

data class RatingSummary (
    val averageRating: Double,
    val ratingCount: Int
)