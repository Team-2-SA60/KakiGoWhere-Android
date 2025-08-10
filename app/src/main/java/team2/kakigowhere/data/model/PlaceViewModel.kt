package team2.kakigowhere.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import team2.kakigowhere.data.api.RetrofitClient

class PlaceViewModel : ViewModel() {
    private val _places = MutableLiveData<List<PlaceDetailDTO>>()
    val places: LiveData<List<PlaceDetailDTO>> = _places

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadPlaces() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getPlaces()
                if (response.isSuccessful) _places.value = response.body()
                else throw Exception ("API Error ${response.code()}")
            } catch (t: Throwable) {
                _error.value = t.message
            }
        }
    }
}
