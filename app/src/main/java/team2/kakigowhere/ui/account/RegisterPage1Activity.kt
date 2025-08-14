package team2.kakigowhere

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import team2.kakigowhere.data.api.RetrofitClient
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
            finish()
        }
    }

    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {
            val name =
                binding.nameEditText.text
                    .toString()
                    .trim()
            val email =
                binding.emailEditText.text
                    .toString()
                    .trim()
            val password =
                binding.passwordEditText.text
                    .toString()
                    .trim()
            val confirmPassword =
                binding.confirmPasswordEditText.text
                    .toString()
                    .trim()

            lifecycleScope.launch {
                if (validateInputs(name, email, password, confirmPassword)) {
                    val intent =
                        Intent(this@RegisterPage1Activity, RegisterPage2Activity::class.java).apply {
                            putExtra("name", name)
                            putExtra("email", email)
                            putExtra("password", password)
                        }
                    startActivity(intent)
                }
            }
        }
    }

    private suspend fun validateInputs(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): Boolean {
        var isValid = true

        // delete wrong info
        hideEmailError()
        hidePasswordError()

        // check empty
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showEmailError("Please fill in all fields")
            return false
        }

        // check email format
        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()
        ) {
            showEmailError("Invalid email format")
            isValid = false
        }

        // use backend to check duplicate email
        val response = RetrofitClient.api.checkEmailExists(email)
        if (response.isSuccessful && response.body() == true) {
            showEmailError("Email already registered")
            isValid = false
        }

        // check password length
        if (password.length < 6) {
            showPasswordError("Password must be at least 6 characters")
            isValid = false
        }

        // check password match
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
