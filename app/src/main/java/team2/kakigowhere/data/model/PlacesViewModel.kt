package team2.kakigowhere.data.model

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import team2.kakigowhere.data.PlacesRepository
import team2.kakigowhere.data.model.PlaceDTO

class PlacesViewModel : ViewModel() {
    private val repo = PlacesRepository()

    private val _places = MutableLiveData<List<PlaceDTO>>(emptyList())
    val places: LiveData<List<PlaceDTO>> = _places

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun loadPlaces() {
        viewModelScope.launch {
            try {
                _places.value = repo.fetchPlaces()
            } catch (t: Throwable) {
                _error.value = t.message
            }
        }
    }
}
