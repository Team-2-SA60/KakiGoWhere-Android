package team2.kakigowhere.ui

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import team2.kakigowhere.R
import team2.kakigowhere.data.api.RetrofitClient
import team2.kakigowhere.data.model.Itinerary
import team2.kakigowhere.data.model.ItineraryViewModel
import java.time.LocalDate

class ItineraryCreateFragment : Fragment() {

    private lateinit var email: String
    private val itineraryViewModel: ItineraryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_itinerary_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up calendar state
        val datePicker = view.findViewById<DatePicker>(R.id.calendar_view)
        val setCalendar = Calendar.getInstance().apply {
            set(
                LocalDate.now().year,
                LocalDate.now().monthValue - 1,
                LocalDate.now().dayOfMonth
            )}
        datePicker.minDate = setCalendar.timeInMillis

        // get user email from fragment args
        email = ItineraryCreateFragmentArgs.fromBundle(requireArguments()).touristEmail

        // enable button to create itinerary only if it has a name
        val createBtn = view.findViewById<Button>(R.id.create_itinerary)
        val name = view.findViewById<EditText>(R.id.itinerary_name)
        name.doOnTextChanged { _, _, _, count ->
            createBtn.isEnabled = count > 0
        }

        initCreateItinerary(createBtn, name, datePicker, email)
    }

    private fun initCreateItinerary(
        createBtn: Button,
        name: EditText,
        calendar: DatePicker,
        email: String
    ) {
        createBtn.setOnClickListener {
            if (createBtn.isEnabled) {
                var itineraryName = name.text.toString()
                var date = LocalDate.of(calendar.year, calendar.month + 1, calendar.dayOfMonth)
                var itinerary = Itinerary(title = itineraryName, startDate = date.toString())

                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.api.createItinerary(email, itinerary)
                        if (response.isSuccessful) {
                            itineraryViewModel.loadItineraries(email)
                            findNavController().navigate(
                                ItineraryCreateFragmentDirections.actionCreateItineraryFragmentToSavedFragment()
                            )
                        } else {
                            Toast.makeText(requireContext(), "Error making itinerary", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.d("API Error", "Create itinerary unsuccessful")
                        Log.d("API Error", e.toString())
                    }
                }
            }
        }
    }

}