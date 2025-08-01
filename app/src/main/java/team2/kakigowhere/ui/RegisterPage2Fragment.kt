package team2.kakigowhere.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import team2.kakigowhere.R
import team2.kakigowhere.data.auth.AuthService
import team2.kakigowhere.databinding.FragmentRegisterPage2Binding

class RegisterPage2Fragment : Fragment() {

    private var _binding: FragmentRegisterPage2Binding? = null
    private val binding get() = _binding!!
    private val args: RegisterPage2FragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterPage2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButton()
        setupInterestSelection()
        setupSaveButton()
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
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
                // 注册用户
                val result = AuthService.registerUser(
                    name = args.name,
                    email = args.email,
                    password = args.password,
                    interests = selectedInterests
                )

                when (result) {
                    is AuthService.RegisterResult.Success -> {
                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                        // 注册成功后直接进入主页面
                        findNavController().navigate(R.id.action_registerPage2Fragment_to_homeFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}