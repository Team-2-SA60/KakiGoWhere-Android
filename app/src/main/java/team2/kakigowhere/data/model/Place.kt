package team2.kakigowhere.data.model

data class Place(
    var id: Long,
    var name: String,
    var description: String,
    var imagePath: String,
    var url: String,
    var openingHour: String, // to change to LocalTime after testing api
    var closingHour: String, // to change to LocalTime after testing api
    var latitude: Double,
    var longitude: Double,
    var active: Boolean,
)

