package team2.kakigowhere.data.model

data class ItineraryDetails(
    var id: Long,
    var dateString: String, // to change to LocalDate
    var notes: String,
    var sequentialOrder: Int
)
