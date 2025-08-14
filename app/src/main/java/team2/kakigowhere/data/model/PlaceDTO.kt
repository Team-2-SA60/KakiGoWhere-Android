package team2.kakigowhere.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class PlaceDetailDTO(
    val id: Long,
    val googleId: String,
    val name: String,
    val address: String,
    val description: String,
    val imagePath: String,
    val openingDescription: String,
    val latitude: Double,
    val longitude: Double,
    val interestCategories: List<InterestCategory>,
    val openingHours: List<OpeningHours>,
    val placeEvents: List<PlaceEvent>,
    val averageRating: Double,
    val open: Boolean,
    val active: Boolean,
    val url: String,
)

@Parcelize
data class InterestCategory(
    var id: Long,
    var name: String,
    var description: String,
) : Parcelable

data class OpeningHours(
    val id: Long,
    val openDay: Int,
    val openHour: Int,
    val openMinute: Int,
    val closeDay: Int,
    val closeHour: Int,
    val closeMinute: Int,
)

data class PlaceEvent(
    val id: Long,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
)

@Parcelize
data class LoginResponse(
    val id: Long,
    val email: String,
    val name: String,
    val role: String,
    val interestCategories: List<InterestCategory>? = emptyList(),
) : Parcelable
