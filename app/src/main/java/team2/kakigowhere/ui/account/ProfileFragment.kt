package team2.kakigowhere.ui.account

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import team2.kakigowhere.LoginActivity
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.InterestCategoryProvider
import team2.kakigowhere.data.model.TouristUpdateRequest
import team2.kakigowhere.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val prefsName = "shared_prefs"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        // Load and display user name
        binding.etName.setText(prefs.getString("user_name", ""))

        // Load and display interests (robust: supports IDs or legacy names)
        binding.etInterests.setText(resolveInterestDisplay(prefs))

        // Save name (local) and sync to backend
        binding.btnSaveName.setOnClickListener {
            // Validate name
            val inputName =
                binding.etName.text
                    .toString()
                    .trim()
            if (inputName.isEmpty() || !inputName.matches(Regex(".*[a-zA-Z].*"))) {
                Toast.makeText(requireContext(), "Please enter a valid name with letters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save locally
            prefs
                .edit()
                .putString("user_name", inputName)
                .apply()
            Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show()

            // Sync to backend using existing Retrofit client
            val userId = prefs.getLong("user_id", -1L)
            if (userId > 0L) {
                // Prefer IDs; if app had older name-based storage, map names -> IDs
                val currentInterests: List<Long> =
                    run {
                        val idSet = prefs.getStringSet("user_interests", emptySet()) ?: emptySet()
                        val ids = idSet.mapNotNull { it.toLongOrNull() }
                        if (ids.isNotEmpty()) {
                            ids
                        } else {
                            val nameSet = prefs.getStringSet("user_interest_names", emptySet()) ?: idSet
                            val nameToId =
                                InterestCategoryProvider.allCategories
                                    .flatMap { listOf(it.name to it.id, it.description to it.id) }
                                    .associate { (k, v) -> k.lowercase() to v }
                            nameSet.mapNotNull { nameToId[it.lowercase()] }
                        }
                    }

                val currentName = inputName

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val resp =
                            RetrofitClient.api.updateTourist(
                                userId,
                                TouristUpdateRequest(
                                    name = currentName,
                                    interestCategoryIds = currentInterests,
                                ),
                            )
                        if (resp.isSuccessful) {
                            Toast.makeText(requireContext(), "Name synced", Toast.LENGTH_SHORT).show()
                        } else {
                            val errBody =
                                try {
                                    resp.errorBody()?.string()
                                } catch (_: Exception) {
                                    null
                                }
                            Toast
                                .makeText(
                                    requireContext(),
                                    "Sync failed: HTTP ${resp.code()} ${errBody?.take(200) ?: ""}",
                                    Toast.LENGTH_LONG,
                                ).show()
                        }
                    } catch (e: Exception) {
                        Toast
                            .makeText(
                                requireContext(),
                                "Failed to sync name: ${e.message}",
                                Toast.LENGTH_LONG,
                            ).show()
                    }
                }
            }
        }

        // Navigate to ChangeCategoriesFragment
        binding.btnChangeCategories.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changeCategoriesFragment)
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
        // Refresh interests from SharedPreferences (robust)
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        binding.etInterests.setText(resolveInterestDisplay(prefs))
    }

    private fun resolveInterestDisplay(prefs: SharedPreferences): String {
        // Map category id -> user-facing label
        val idToLabel =
            InterestCategoryProvider.allCategories
                .associate { it.id to it.description }

        // Map various name forms -> user-facing label (case-insensitive)
        val nameToLabel =
            InterestCategoryProvider.allCategories
                .flatMap { listOf(it.name to it.description, it.description to it.description) }
                .associate { (k, v) -> k.lowercase() to v }

        // 1) Preferred: IDs stored in user_interests
        val idSet = prefs.getStringSet("user_interests", emptySet()) ?: emptySet()
        val fromIds = idSet.mapNotNull { it.toLongOrNull()?.let(idToLabel::get) }
        if (fromIds.isNotEmpty()) return fromIds.joinToString(", ")

        // 2) Newer fallback: explicit names set (if present)
        val nameSet = prefs.getStringSet("user_interest_names", emptySet()) ?: emptySet()
        val fromNames = nameSet.mapNotNull { nameToLabel[it.lowercase()] }
        if (fromNames.isNotEmpty()) return fromNames.joinToString(", ")

        // 3) Legacy fallback: names might have been stored in user_interests
        val legacy = idSet.mapNotNull { nameToLabel[it.lowercase()] }
        if (legacy.isNotEmpty()) return legacy.joinToString(", ")

        // Nothing to show
        return ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
