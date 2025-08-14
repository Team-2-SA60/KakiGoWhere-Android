package team2.kakigowhere.data.model

data class RegisterRequestDTO(
    val name: String,
    val email: String,
    val password: String,
    val interestCategoryIds: List<Long>
)

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

