package team2.kakigowhere

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import team2.kakigowhere.data.auth.AuthService
import team2.kakigowhere.databinding.ActivityRegisterPage1Binding

class RegisterPage1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterPage1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPage1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackButton()
        setupRegisterButton()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish() // 返回上一页
        }
    }

    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if (validateInputs(name, email, password, confirmPassword)) {
                // 跳转到注册第二页，传递数据
                val intent = Intent(this, RegisterPage2Activity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                startActivity(intent)
            }
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        // 清除之前的错误信息
        hideEmailError()
        hidePasswordError()

        // 检查空字段
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showEmailError("Please fill in all fields")
            return false
        }

        // 检查邮箱格式
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showEmailError("Invalid email format")
            isValid = false
        }

        // 检查邮箱是否已存在
        if (AuthService.isEmailExists(email)) {
            showEmailError("Email already registered")
            isValid = false
        }

        // 检查密码长度
        if (password.length < 6) {
            showPasswordError("Password must be at least 6 characters")
            isValid = false
        }

        // 检查密码匹配
        if (password != confirmPassword) {
            showPasswordError("Passwords do not match")
            isValid = false
        }

        return isValid
    }

    private fun showEmailError(message: String) {
        binding.emailErrorMessageText.text = message
        binding.emailErrorMessageText.visibility = View.VISIBLE
    }

    private fun hideEmailError() {
        binding.emailErrorMessageText.visibility = View.INVISIBLE
    }

    private fun showPasswordError(message: String) {
        binding.passwordErrorMessageText.text = message
        binding.passwordErrorMessageText.visibility = View.VISIBLE
    }

    private fun hidePasswordError() {
        binding.passwordErrorMessageText.visibility = View.INVISIBLE
    }
}