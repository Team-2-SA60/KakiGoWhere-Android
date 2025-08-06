package team2.kakigowhere.data.model

class User(
    val email: String,
    val password: String,
    val name: String = "",
    val interests: List<String> = emptyList()
)
