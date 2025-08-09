package team2.kakigowhere.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName

data class PlaceDTO(
    val id: Long,
    val googleId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val isActive: Boolean,
    val isOpen: Boolean,
    val averageRating: Double,
    val interestCategories: List<InterestCategory>
)

data class PlaceDetailDTO(
    val id: Long,
    val googleId: String,
    val name: String,
    val address: String,
    val description: String,
    val imagePath: String,
    @SerializedName("url") val URL: String,
    val openingDescription: String,
    val latitude: Double,
    val longitude: Double,
    val isActive: Boolean,
    val interestCategories: List<InterestCategory>,
    val openingHours: List<OpeningHours>,
    val placeEvents: List<PlaceEvent>,
    val averageRating: Double,
    val isOpen: Boolean
)
@Parcelize
data class InterestCategory(
    var id: Long,
    var name: String,
    var description: String
) : Parcelable

data class OpeningHours(
    val id: Long,
    val openDay: Int,
    val openHour: Int,
    val openMinute: Int,
    val closeDay: Int,
    val closeHour: Int,
    val closeMinute: Int
)

data class PlaceEvent(
    val id: Long,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String
)

@Parcelize
data class LoginResponse(
    val id: Long,
    val email: String,
    val name: String,
    val role: String,
    @SerializedName("interestCategories")
    val interestsCategories: List<InterestCategory>? = emptyList()
) :Parcelable
