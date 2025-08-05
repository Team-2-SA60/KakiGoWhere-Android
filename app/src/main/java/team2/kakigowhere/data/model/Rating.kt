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

)   :Parcelable {
}

