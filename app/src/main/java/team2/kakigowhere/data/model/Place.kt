package team2.kakigowhere.data.model

data class Place(
    var id: Long,
    var googleId: String,
    var name: String,
    var address: String,
    var latitude: Double,
    var longitude: Double,
    var isActive: Boolean,
    var isOpen: Boolean,
    var averageRating: Double,
    var interestCategories: List<InterestCategory>
)

data class InterestCategory(
    var id: Long,
    var name: String,
    var description: String
)

