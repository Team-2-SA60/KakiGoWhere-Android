package team2.kakigowhere.data.model

data class ItineraryDTO(
    var id: Long,
    var title: String,
    var startDate: String, // to change to LocalDate
    var days: Long,
    var placeDisplayId: Long
)
