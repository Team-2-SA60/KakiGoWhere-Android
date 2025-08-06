package team2.kakigowhere

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Response
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // If already logged in, skip login screen
        val prefs = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        if (prefs.contains("user_id")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }


        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    // Send credentials as JSON map { "email": "...", "password": "..." }
                    val response: Response<LoginResponse> = RetrofitClient.api.login(
                        mapOf(
                            "email" to email,
                            "password" to password
                        )
                    )

                    if (response.isSuccessful && response.body() != null) {
                        val user = response.body()!!

                        val interests = user.interestsCategories ?: emptyList()
                        val interestsSet = interests.map { it.name }.toSet()


                        // Save user info in SharedPreferences
                        val prefs = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
                        prefs.edit().apply {
                            putLong("user_id", user.id)
                            putString("user_email", user.email)
                            putString("user_name", user.name)
                            putString("user_role", user.role)
                            // Save interests as a string set (names of categories)
                            putStringSet("user_interests", interestsSet)
                            apply()
                        }

                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login failed: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Network error: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}