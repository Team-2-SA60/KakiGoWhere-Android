package team2.kakigowhere.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//@Parcelize
//// TODO: this class is not used so far, consider removing
//data class Place(
//    var id: Long,
//    var name: String,
//    var description: String,
//    var imagePath: String,
//    var url: String,
//    var openingHour: String, // to change to LocalTime after testing api
//    var closingHour: String, // to change to LocalTime after testing api
//    var latitude: Double,
//    var longitude: Double,
//    var active: Boolean
//)   :Parcelable

@Parcelize
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
) : Parcelable

@Parcelize
data class InterestCategory(
    var id: Long,
    var name: String,
    var description: String
) : Parcelable
