package team2.kakigowhere.ui.itinerary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.ItineraryDetail
import team2.kakigowhere.data.model.ItineraryViewModel

class ItineraryEditFragment : Fragment() {

    private val itineraryViewModel: ItineraryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_itinerary_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireContext().getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val email = prefs.getString("user_email", "") ?: ""

        // get itinerary detail from fragment args
        val detail = ItineraryEditFragmentArgs.fromBundle(requireArguments()).itineraryDetail

        view.findViewById<TextView>(R.id.id_title).text = detail.placeTitle
        view.findViewById<TextView>(R.id.id_date).text = detail.date

        val notes = view.findViewById<EditText>(R.id.notes)
        notes.setText(detail.notes)

        val update = view.findViewById<Button>(R.id.update)
        val delete = view.findViewById<Button>(R.id.delete)

        update.setOnClickListener {
            val updatedDetail = ItineraryDetail(
                id = detail.id,
                date = detail.date,
                notes = notes.text.toString(),
                sequentialOrder = detail.sequentialOrder
            )
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.editItineraryItem(detail.id, updatedDetail)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Updated itinerary details", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                } catch (e: Exception) {
                    Log.d("API Error", e.printStackTrace().toString())
                }
            }
        }

        delete.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.api.deleteItineraryItem(detail.id)
                    if (response.isSuccessful) {
                        itineraryViewModel.loadItineraries(email)
                        Toast.makeText(requireContext(), "Deleted itinerary item", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                } catch (e: Exception) {
                    Log.d("API Error", e.printStackTrace().toString())
                }
            }
        }
    }
}