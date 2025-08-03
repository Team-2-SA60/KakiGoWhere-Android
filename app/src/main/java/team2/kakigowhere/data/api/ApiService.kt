package team2.kakigowhere.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import team2.kakigowhere.data.model.Place
import team2.kakigowhere.data.model.PlaceDTO

interface ApiService {
    @GET("places")
    suspend fun getPlaces(): Response<List<Place>>

    @GET("places/id/{placeId}")
    suspend fun getPlaceDetail(@Path("placeId") placeId: Long): Response<PlaceDTO>
}
