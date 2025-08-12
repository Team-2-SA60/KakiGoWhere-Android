package team2.kakigowhere.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.TouristUpdateRequest
import team2.kakigowhere.data.model.InterestCategoryProvider
import team2.kakigowhere.databinding.FragmentChangeCategoriesBinding

class ChangeCategoriesFragment : Fragment() {

    private var _binding: FragmentChangeCategoriesBinding? = null
    private val binding get() = _binding!!
    private val prefsName = "shared_prefs"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Load SharedPreferences
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        // Retrieve saved interests as a mutable set (IDs as strings)
        val savedSet = prefs.getStringSet("user_interests", emptySet())?.toMutableSet() ?: mutableSetOf()

        // Selected IDs from saved set
        val selectedIds = savedSet.mapNotNull { it.toLongOrNull() }.toMutableSet()

        // Get all categories sorted by description
        val categories = InterestCategoryProvider.allCategories.sortedBy { it.description }

        // Set up RecyclerView and adapter
        val adapter = CategoryAdapter(categories, selectedIds)
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = adapter

        // Save: call backend via RetrofitClient, then persist locally on success
        binding.btnSaveCategories.setOnClickListener {
            val chosenIds = adapter.getSelectedIds().toList()
            if (chosenIds.isEmpty()) {
                Toast.makeText(requireContext(), "Please choose at least 1 category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val chosenStr = chosenIds.map { it.toString() }.toSet()

            val prefsEditor = prefs.edit()
            val userId = prefs.getLong("user_id", -1L)
            val name = prefs.getString("user_name", "") ?: ""

            if (userId <= 0L) {
                Toast.makeText(requireContext(), "Missing user id; please re-login.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val resp = RetrofitClient.api.updateTourist(
                        userId,
                        TouristUpdateRequest(name = name, interestCategoryIds = chosenIds)
                    )
                    if (resp.isSuccessful) {
                        // Persist locally after successful update

                        val chosenNames = categories
                            .filter { it.id in chosenIds }
                            .map { it.description } // use the label ML expects (e.g., "Museums")
                            .toSet()

                        prefs.edit()
                            .putStringSet("user_interests", chosenStr)
                            .putStringSet("user_interest_names", chosenNames)
                            .remove("reco_interests_key") // invalidate recomms cache
                            .remove("reco_ids_json")
                            .apply()

                        Toast.makeText(requireContext(), "Interests synced", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        val errBody = try { resp.errorBody()?.string() } catch (_: Exception) { null }
                        Toast.makeText(
                            requireContext(),
                            "Sync failed: HTTP ${resp.code()} ${errBody?.take(200) ?: ""}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to sync: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}