package team2.kakigowhere.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ItineraryDTO(
    var id: Long,
    var title: String,
    var startDate: String, // to change to LocalDate
    var days: Long,
    var placeDisplayId: Long
) {
    // convert date in String to LocalDate
    //val startDate: LocalDate get() = LocalDate.parse(startDateString, DateTimeFormatter.ISO_LOCAL_DATE)

    // get last date of itinerary
    fun getLastDate(): String {
        val firstDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE)
        val lastDate = firstDate.plusDays(days - 1)
        return lastDate.toString()
    }
}
