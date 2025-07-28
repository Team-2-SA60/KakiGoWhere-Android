package team2.kakigowhere.data.model

import java.time.LocalTime

data class Place (
    var id: Long,
    var name: String,
    var description: String,
    var imagePath: String,
    var URL: String,
    var openingHour: LocalTime,
    var closingHour: LocalTime,
    var latitude: Double,
    var longitude: Double,
    var activeStatus: Boolean
)