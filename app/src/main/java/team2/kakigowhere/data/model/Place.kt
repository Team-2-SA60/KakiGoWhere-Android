package team2.kakigowhere.data.model

data class Place(
    var id: Long,
    var name: String,
    var description: String,
    var imagePath: String,
    var URL: String,
    var openingHour: String, // to change to LocalTime after testing api
    var closingHour: String, // to change to LocalTime after testing api
    var latitude: Double,
    var longitude: Double,
    var activeStatus: Boolean,
)
