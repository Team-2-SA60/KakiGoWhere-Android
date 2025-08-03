package team2.kakigowhere.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class ItineraryDetailDTO(
    var id: Long,
    var date: String, // to change to LocalDate
    var notes: String,
    var sequentialOrder: Int,
    var placeId: Long,
    var placeTitle: String,
    var placeIsOpen: Boolean,
    var placeOpenHours: String
) {
    // return date as LocalDate
    val dateActual: LocalDate get() = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
}