package team2.kakigowhere.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Itinerary(
    var id: Long? = null,
    var title: String,
    var startDate: String
)

data class ItineraryDTO(
    var id: Long,
    var title: String,
    var startDate: String,
    var days: Long,
    var placeDisplayId: Long
) {
    // convert date in String to LocalDate
    val getStartDate: LocalDate get() = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)

    // get last date of itinerary
    fun getLastDate(): String {
        val firstDate = getStartDate
        val lastDate = firstDate.plusDays(days - 1)
        return lastDate.toString()
    }
}

data class ItineraryDetailDTO(
    var id: Long,
    var date: String,
    var notes: String,
    var sequentialOrder: Int,
    var placeId: Long,
    var placeTitle: String,
    var placeIsOpen: Boolean,
    var placeOpenHours: String
) {
    // convert date in String to LocalDate
    val itemDate: LocalDate get() = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
}
