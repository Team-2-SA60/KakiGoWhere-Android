package team2.kakigowhere.data.api

import retrofit2.Response
import retrofit2.http.GET
import team2.kakigowhere.data.model.Place

interface ApiService {

    @GET("api/places")
    suspend fun getPlaces(): Response<List<Place>>
}