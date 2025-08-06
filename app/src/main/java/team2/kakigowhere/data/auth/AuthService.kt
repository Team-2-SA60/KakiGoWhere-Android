package team2.kakigowhere.data.auth

import team2.kakigowhere.data.model.User

object AuthService {

    // 模拟用户数据库 - 使用MutableList以便添加新用户
    private val users = mutableListOf(
        User("admin@kakigowhere.com", "admin123", "Admin User"),
        User("user@kakigowhere.com", "user123", "Regular User"),
        User("test@kakigowhere.com", "test123", "Test User")
    )

    sealed class AuthResult {
        object Success : AuthResult()
        object EmailNotFound : AuthResult()
        object WrongPassword : AuthResult()
    }

    sealed class RegisterResult {
        object Success : RegisterResult()
        object EmailAlreadyExists : RegisterResult()
        object InvalidEmail : RegisterResult()
    }

    fun authenticate(email: String, password: String): AuthResult {
        val user = users.find { it.email.equals(email, ignoreCase = true) }

        return when {
            user == null -> AuthResult.EmailNotFound
            user.password != password -> AuthResult.WrongPassword
            else -> AuthResult.Success
        }
    }

    fun registerUser(name: String, email: String, password: String, interests: List<String>): RegisterResult {
        // 检查邮箱是否已存在
        if (users.any { it.email.equals(email, ignoreCase = true) }) {
            return RegisterResult.EmailAlreadyExists
        }

        // 简单的邮箱格式验证
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return RegisterResult.InvalidEmail
        }

        // 添加新用户
        val newUser = User(email, password, name, interests)
        users.add(newUser)

        return RegisterResult.Success
    }

    fun isEmailExists(email: String): Boolean {
        return users.any { it.email.equals(email, ignoreCase = true) }
    }
}