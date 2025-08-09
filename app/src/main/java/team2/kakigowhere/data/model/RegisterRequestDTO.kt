package team2.kakigowhere.data.model

data class RegisterRequestDTO(
    val name: String,
    val email: String,
    val password: String,
    val interestCategoryIds: List<Long>
)