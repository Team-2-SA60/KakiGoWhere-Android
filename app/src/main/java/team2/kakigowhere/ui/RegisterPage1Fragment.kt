package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import team2.kakigowhere.R
import team2.kakigowhere.data.auth.AuthService
import team2.kakigowhere.databinding.FragmentRegisterPage1Binding

class RegisterPage1Fragment : Fragment() {

    private var _binding: FragmentRegisterPage1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterPage1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()
        setupRegisterButton()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRegisterButton() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()
            val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()

            if (validateInputs(name, email, password, confirmPassword)) {
                // 传递数据到第二页
                val action = RegisterPage1FragmentDirections.actionRegisterPage1FragmentToRegisterPage2Fragment(
                    name = name,
                    email = email,
                    password = password
                )
                binding.findNavController().navigate(action)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}