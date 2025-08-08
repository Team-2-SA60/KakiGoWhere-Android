package team2.kakigowhere.data.model

data class RegisterResponseDTO(
    val id: Long,
    val name: String,
    val email: String,
    val interestCategories: List<InterestCategory>
)