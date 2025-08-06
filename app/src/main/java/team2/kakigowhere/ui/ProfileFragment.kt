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
import team2.kakigowhere.LoginActivity
import team2.kakigowhere.R
import team2.kakigowhere.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val prefsName = "shared_prefs"

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

        // Load and display user name
        binding.etName.setText(prefs.getString("user_name", ""))

        // Load and display interests (as comma list)
        val interestsSet = prefs.getStringSet("user_interests", emptySet()) ?: emptySet()
        binding.etInterests.setText(interestsSet.joinToString(", "))

        // Save name
        binding.btnSaveName.setOnClickListener {
            prefs.edit()
                .putString("user_name", binding.etName.text.toString())
                .apply()
            Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show()
        }

        // Navigate to ChangeCategoriesFragment
        binding.btnChangeCategories.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changeCategoriesFragment)
        }

        // Navigate to RatingsFragment
        binding.btnViewRatings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ratingFragment)
        }

        // Log out
        binding.btnLogout.setOnClickListener {
            prefs.edit().clear().apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh interests from SharedPreferences
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val updatedSet = prefs.getStringSet("user_interests", emptySet()) ?: emptySet()
        binding.etInterests.setText(updatedSet.joinToString(", "))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}