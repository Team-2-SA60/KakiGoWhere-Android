package team2.kakigowhere.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import team2.kakigowhere.data.api.RetrofitClient

class ItineraryViewModel : ViewModel() {

    private val _itineraries = MutableLiveData<List<ItineraryDTO>>()
    val itineraries: LiveData<List<ItineraryDTO>> = _itineraries

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadItineraries(email: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getItineraries(email)
                if (response.isSuccessful) _itineraries.value = response.body()
                else throw Exception ("API Error ${response.code()}")
            } catch (t: Throwable) {
                _error.value = t.message
            }
        }
    }
}