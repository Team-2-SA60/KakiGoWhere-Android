package team2.kakigowhere.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import team2.kakigowhere.data.model.Itinerary
import team2.kakigowhere.data.model.ItineraryDTO
import team2.kakigowhere.data.model.ItineraryDetail
import team2.kakigowhere.data.model.ItineraryDetailDTO
import team2.kakigowhere.data.model.LoginResponse
import team2.kakigowhere.data.model.PlaceDetailDTO
import team2.kakigowhere.data.model.RatingItem
import team2.kakigowhere.data.model.RatingRequest
import team2.kakigowhere.data.model.RatingSummary
import team2.kakigowhere.data.model.RegisterRequestDTO
import team2.kakigowhere.data.model.RegisterResponseDTO
import team2.kakigowhere.data.model.TouristUpdateRequest

interface ApiService {

    // places api

    @GET("places")
    suspend fun getPlaces(): Response<List<PlaceDetailDTO>>

    @GET("places/id/{placeId}")
    suspend fun getPlaceDetail(
        @Path("placeId") placeId: Long
    ): Response<PlaceDetailDTO>

    // ratings api

    @GET("ratings/{placeId}/summary")
    suspend fun getRatingSummary(
        @Path("placeId") placeId: Long
    ): Response<RatingSummary>

    @GET("ratings/{placeId}/me")
    suspend fun getMyRating(
        @Path("placeId") placeId: Long,
        @Query("touristId") touristId: Long
    ): Response<RatingItem>

    @GET("ratings/{placeId}/others")
    suspend fun getOtherRatings(
        @Path("placeId") placeId: Long,
        @Query("touristId") touristId: Long
    ): Response<List<RatingItem>>

    @POST("ratings/{placeId}")
    suspend fun submitOrUpdateRating(
        @Path("placeId") placeId: Long,
        @Query("touristId") touristId: Long,
        @Body request: RatingRequest
    ): Response<RatingItem>

    // itinerary api

    @GET("itinerary")
    suspend fun getItineraries(
        @Header("user-email") email: String
    ): Response<List<ItineraryDTO>>

    @GET("itinerary/detail/{itineraryId}")
    suspend fun getItineraryDetails(
        @Path("itineraryId") itineraryId: Long
    ): Response<List<ItineraryDetailDTO>>

    @POST("itinerary/create")
    suspend fun createItinerary(
        @Header("user-email") email: String,
        @Body itinerary: Itinerary
    ): Response<Unit>

    @DELETE("itinerary/delete/{itineraryId}")
    suspend fun deleteItinerary(
        @Path("itineraryId") itineraryId: Long
    ): Response<Unit>

    @PUT("itinerary/detail/add/day/{itineraryId}")
    suspend fun addItineraryDay(
        @Path("itineraryId") itineraryId: Long,
        @Body itineraryDetail: ItineraryDetail
    ): Response<Unit>

    @DELETE("itinerary/detail/delete/day/{itineraryId}")
    suspend fun deleteItineraryDay(
        @Path("itineraryId") itineraryId: Long,
        @Query("lastDate") date: String
    ): Response<Unit>

    @PUT("itinerary/detail/add/{itineraryId}")
    suspend fun addItemToItinerary(
        @Path("itineraryId") itineraryId: Long,
        @Query("placeId") placeId: Long,
        @Body itineraryDetail: ItineraryDetail
    ): Response<Unit>

    @PUT("itinerary/detail/edit/{detailId}")
    suspend fun editItineraryItem(
        @Path("detailId") detailId: Long,
        @Body itineraryDetail: ItineraryDetail
    ): Response<Unit>

    @DELETE("itinerary/detail/delete/{detailId}")
    suspend fun deleteItineraryItem(
        @Path("detailId") detailId: Long
    ): Response<Unit>

    // account api

    @POST("auth/login")
    suspend fun login(
        @Body credentials: Map<String, String>
    ): Response<LoginResponse>

    @GET("tourist/check-email")
    suspend fun checkEmailExists(
        @Query("email") email: String
    ): Response<Boolean>

    @POST("tourist/register")
    suspend fun registerTourist(
        @Body request: RegisterRequestDTO
    ): Response<RegisterResponseDTO>

    @PUT("tourist/{touristId}")
    suspend fun updateTourist(
        @Path("touristId") touristId: Long,
        @Body request: TouristUpdateRequest
    ): Response<Unit>

}
