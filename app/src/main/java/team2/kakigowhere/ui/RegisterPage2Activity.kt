package team2.kakigowhere

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import team2.kakigowhere.data.auth.AuthService
import team2.kakigowhere.databinding.ActivityRegisterPage2Binding

class RegisterPage2Activity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterPage2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPage2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackButton()
        setupInterestSelection()
        setupSaveButton()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish() // 返回上一页
        }
    }

    private fun setupInterestSelection() {
        // 设置每个兴趣选项的点击事件
        binding.foodLayout.setOnClickListener {
            binding.foodCheckBox.isChecked = !binding.foodCheckBox.isChecked
        }

        binding.entertainmentLayout.setOnClickListener {
            binding.entertainmentCheckBox.isChecked = !binding.entertainmentCheckBox.isChecked
        }

        binding.sceneryLayout.setOnClickListener {
            binding.sceneryCheckBox.isChecked = !binding.sceneryCheckBox.isChecked
        }

        binding.cultureLayout.setOnClickListener {
            binding.cultureCheckBox.isChecked = !binding.cultureCheckBox.isChecked
        }

        binding.heritageLayout.setOnClickListener {
            binding.heritageCheckBox.isChecked = !binding.heritageCheckBox.isChecked
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val selectedInterests = getSelectedInterests()

            if (validateInterests(selectedInterests)) {
                // 获取从第一页传递过来的数据
                val name = intent.getStringExtra("name") ?: ""
                val email = intent.getStringExtra("email") ?: ""
                val password = intent.getStringExtra("password") ?: ""

                // 注册用户
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
                }
            }
        }
    }

    private fun getSelectedInterests(): List<String> {
        val interests = mutableListOf<String>()

        if (binding.foodCheckBox.isChecked) interests.add("Food")
        if (binding.entertainmentCheckBox.isChecked) interests.add("Entertainment")
        if (binding.sceneryCheckBox.isChecked) interests.add("Scenery")
        if (binding.cultureCheckBox.isChecked) interests.add("Culture")
        if (binding.heritageCheckBox.isChecked) interests.add("Heritage")

        return interests
    }

    private fun validateInterests(interests: List<String>): Boolean {
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