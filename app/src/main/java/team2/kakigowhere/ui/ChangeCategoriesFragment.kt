package team2.kakigowhere.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
        val prefs = requireContext()
            .getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        // Retrieve saved interests as a mutable set
        val savedSet = prefs.getStringSet("user_interests", emptySet())
            ?.toMutableSet() ?: mutableSetOf()

        // Get all categories sorted by description
        val categories = InterestCategoryProvider.allCategories
            .sortedBy { it.description }

        // Set up RecyclerView and adapter
        val adapter = CategoryAdapter(categories, savedSet)
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = adapter

        // Save button: persist and close fragment
        binding.btnSaveCategories.setOnClickListener {
            prefs.edit()
                .putStringSet("user_interests", adapter.getSelected())
                .apply()
            Toast.makeText(requireContext(), "Interests updated", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}