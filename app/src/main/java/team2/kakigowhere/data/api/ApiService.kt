package team2.kakigowhere.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import team2.kakigowhere.data.model.ItineraryDTO
import team2.kakigowhere.data.model.ItineraryDetailDTO
import team2.kakigowhere.data.model.Place

interface ApiService {
    @GET("places")
    suspend fun getPlaces(): Response<List<Place>>

    @GET("itinerary/{email}")
    suspend fun getItineraries(@Path("email") email: String): Response<List<ItineraryDTO>>

    @GET("itinerary/detail/{id}")
    suspend fun getItineraryDetails(@Path("id") id: Long): Response<List<ItineraryDetailDTO>>

    @POST("itinerary/create")
    suspend fun createItinerary()
}
