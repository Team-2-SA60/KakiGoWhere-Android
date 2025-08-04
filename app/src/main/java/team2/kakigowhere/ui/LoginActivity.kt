package team2.kakigowhere

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import team2.kakigowhere.data.auth.AuthService
import team2.kakigowhere.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLoginButton()
        setupSignUpButton()
    }

    private fun setupLoginButton() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showError("Please fill in all fields")
                return@setOnClickListener
            }

            performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        val result = AuthService.authenticate(email, password)

        when (result) {
            is AuthService.AuthResult.Success -> {
                hideError()
                // 跳转到主页面
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // 结束登录Activity，防止返回
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            }
            is AuthService.AuthResult.EmailNotFound -> {
                showError("Email cannot found")
            }
            is AuthService.AuthResult.WrongPassword -> {
                showError("Password is wrong")
            }
        }
    }

    private fun setupSignUpButton() {
        binding.signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterPage1Activity::class.java)
            startActivity(intent)
        }
    }

    private fun showError(message: String) {
        binding.errorMessageText.text = message
        binding.errorMessageText.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.errorMessageText.visibility = View.INVISIBLE
    }
}