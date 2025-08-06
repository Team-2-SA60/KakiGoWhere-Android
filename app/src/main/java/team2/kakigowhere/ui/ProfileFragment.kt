package team2.kakigowhere.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import team2.kakigowhere.databinding.FragmentProfileBinding
import team2.kakigowhere.LoginActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val prefsName = "kaki_prefs"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        // Load existing values
        binding.etName.setText(prefs.getString("user_name", ""))
        binding.etInterests.setText(prefs.getString("user_interests", ""))

        // Save name locally
        binding.btnSaveName.setOnClickListener {
            val name = binding.etName.text.toString()
            prefs.edit().putString("user_name", name).apply()
            Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show()
        }

        // Save interests locally
        binding.btnSaveInterests.setOnClickListener {
            val interests = binding.etInterests.text.toString()
            prefs.edit().putString("user_interests", interests).apply()
            Toast.makeText(requireContext(), "Interests updated", Toast.LENGTH_SHORT).show()
        }

        // Navigate to RatingsFragment
        binding.btnViewRatings.setOnClickListener {
            findNavController().navigate(team2.kakigowhere.R.id.action_profileFragment_to_ratingFragment)
        }

        // Log out
        binding.btnLogout.setOnClickListener {
            requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}