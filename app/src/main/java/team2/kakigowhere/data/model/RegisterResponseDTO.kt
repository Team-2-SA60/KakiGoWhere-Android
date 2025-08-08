package team2.kakigowhere.data.model

data class RegisterResponseDTO(
    val id: Long,
    val name: String,
    val email: String,
    val interestCategories: List<InterestCategory>

)

data class TouristUpdateRequest(
    val name: String,
    val interestCategoryIds: List<Long>
)

data class InterestCategoryDto(
    val id: Long,
    val name: String,
    val description: String
)

//data class TouristResponse(
//    val id: Long,
//    val email: String,
//    val name: String,
//    val interestCategories: List<InterestCategoryDto>
//)

