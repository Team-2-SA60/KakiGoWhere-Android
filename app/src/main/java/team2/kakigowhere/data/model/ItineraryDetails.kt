package team2.kakigowhere.data.model

import java.time.LocalDate

data class ItineraryDetails(
    var id: Long,
    var date: LocalDate,
    var notes: String,
    var sequentialOrder: Int
)
