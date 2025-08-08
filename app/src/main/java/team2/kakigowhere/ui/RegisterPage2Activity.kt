package team2.kakigowhere

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.auth.AuthService
import team2.kakigowhere.data.model.InterestCategoryProvider
import team2.kakigowhere.data.model.RegisterRequestDTO
import team2.kakigowhere.databinding.ActivityRegisterPage2Binding
import team2.kakigowhere.ui.CategoryAdapter

class RegisterPage2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterPage2Binding
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPage2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackButton()
        setupRecyclerView()
        setupSaveButton()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish() // 返回上一页
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter(
            categories = InterestCategoryProvider.allCategories,
            selected = mutableSetOf() // save selected id
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val selectedInterestIds = adapter.getSelectedIds()

            if (validateInterests(selectedInterestIds)) {
                // 获取从第一页传递过来的数据
                val name = intent.getStringExtra("name") ?: ""
                val email = intent.getStringExtra("email") ?: ""
                val password = intent.getStringExtra("password") ?: ""

                val request = RegisterRequestDTO(
                    name = name,
                    email = email,
                    password = password,
                    interestCategoryIds = selectedInterestIds
                )

                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.api.registerTourist(request)
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@RegisterPage2Activity,
                                "Registration successful!",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@RegisterPage2Activity, MainActivity::class.java))
                            finish()
                        } else {
                            showError("Registration failed: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        showError("Error: ${e.localizedMessage}")
                    }
                }

                /*
                val result = AuthService.registerUser(
                    name = name,
                    email = email,
                    password = password,
                    interests = selectedInterests
                )

                when (result) {
                    is AuthService.RegisterResult.Success -> {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                        // 注册成功后直接进入主页面
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // 结束注册Activity
                    }
                    is AuthService.RegisterResult.EmailAlreadyExists -> {
                        showError("Email already registered")
                    }
                    is AuthService.RegisterResult.InvalidEmail -> {
                        showError("Invalid email format")
                    }
                }*/
            }
        }
    }

    private fun validateInterests(interests: List<Long>): Boolean {
        hideError()

        return when {
            interests.isEmpty() -> {
                showError("Please select at least 1 interest")
                false
            }
            interests.size > 3 -> {
                showError("Please select no more than 3 interests")
                false
            }
            else -> true
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