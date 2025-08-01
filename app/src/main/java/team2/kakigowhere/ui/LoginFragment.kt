package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import team2.kakigowhere.R
import team2.kakigowhere.data.auth.AuthService
import team2.kakigowhere.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                // 导航到主页面
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
            }
            is AuthService.AuthResult.EmailNotFound -> {
                showError("Email cannot found")
            }
            is AuthService.AuthResult.WrongPassword -> {
                showError("Password is wrong")
            }
        }
    }

    private fun showError(message: String) {
        binding.errorMessageText.text = message
        binding.errorMessageText.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.errorMessageText.visibility = View.INVISIBLE
    }

    private fun setupSignUpButton() {
        binding.signUpButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerPage1Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}