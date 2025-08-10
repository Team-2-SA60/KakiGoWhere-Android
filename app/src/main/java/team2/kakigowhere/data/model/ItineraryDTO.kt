package team2.kakigowhere.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Itinerary(
    var id: Long? = null,
    var title: String,
    var startDate: String
)

data class ItineraryDetail(
    var id: Long? = null,
    var date: String,
    var notes: String = "",
    var sequentialOrder: Int? = null
)

@Parcelize
data class ItineraryDTO(
    var id: Long,
    var title: String,
    var startDate: String,
    var days: Long,
    var placeDisplayId: Long
) : Parcelable {
    // convert date in String to LocalDate
    val getStartDate: LocalDate get() = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)

    // get last date of itinerary
    fun getLastDate(): LocalDate {
        val firstDate = getStartDate
        val lastDate = firstDate.plusDays(days - 1)
        return lastDate
    }
}

@Parcelize
data class ItineraryDetailDTO(
    var id: Long,
    var date: String,
    var notes: String,
    var sequentialOrder: Int,
    var placeId: Long,
    var placeTitle: String,
    var placeIsOpen: Boolean,
    var placeOpenHours: String
) : Parcelable {
    // convert date in String to LocalDate
    val itemDate: LocalDate get() = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
}