package team2.kakigowhere.data.model

import java.time.LocalDate

data class Itinerary (
    var id: Long? = null,
    var title: String,
    var startDate: LocalDate
)

data class CreateItineraryDTO(
    val id: Long? = null,
    val title: String,
    val startDate: String
)

fun Itinerary.toDto(): CreateItineraryDTO {
    return CreateItineraryDTO(
        id = this.id,
        title = this.title,
        startDate = this.startDate.toString()
    )
}