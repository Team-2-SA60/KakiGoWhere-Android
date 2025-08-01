package team2.kakigowhere.data.model

import java.time.LocalDate

data class Itinerary (
    var id: Long,
    var title: String,
    var dateStart: LocalDate
)