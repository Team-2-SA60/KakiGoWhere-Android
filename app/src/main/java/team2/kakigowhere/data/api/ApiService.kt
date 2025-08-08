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
import team2.kakigowhere.data.model.PlaceDTO
import team2.kakigowhere.data.model.PlaceDetailDTO
import team2.kakigowhere.data.model.RatingItem
import team2.kakigowhere.data.model.RatingRequest
import team2.kakigowhere.data.model.RatingSummary

interface ApiService {

    // places api
    @GET("places")
    suspend fun getPlaces(): Response<List<PlaceDTO>>

    @GET("places/id/{placeId}")
    suspend fun getPlaceDetail(
        @Path("placeId") placeId: Long
    ): Response<PlaceDetailDTO>

    @GET("ratings/{placeId}/summary")
    suspend fun getRatingSummary(
        @Path("placeId") placeId: Long
    ): Response<RatingSummary>

    // may be empty as Tourist may not have given rating
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

    @GET("itinerary/{email}")
    suspend fun getItineraries(
        @Path("email") email: String
    ): Response<List<ItineraryDTO>>

    @GET("itinerary/detail/{id}")
    suspend fun getItineraryDetails(
        @Path("id") id: Long
    ): Response<List<ItineraryDetailDTO>>

    @PUT("itinerary/detail/add/{itineraryId}")
    suspend fun addItemToItinerary(
        @Path("itineraryId") id: Long,
        @Query("placeId") placeId: Long,
        @Body itineraryDetail: ItineraryDetail
    ): Response<Unit>

    @PUT("itinerary/detail/add/day/{itineraryId}")
    suspend fun addItineraryDay(
        @Path("itineraryId") id: Long,
        @Body itineraryDetail: ItineraryDetail
    ): Response<Unit>

    @DELETE("itinerary/detail/delete/day/{itineraryId}")
    suspend fun deleteItineraryDay(
        @Path("itineraryId") id: Long,
        @Query("lastDate") date: String
    ): Response<Unit>

    @PUT("itinerary/detail/edit/{detailId}")
    suspend fun editItineraryItem(
        @Path("detailId") id: Long,
        @Body itineraryDetil: ItineraryDetail
    ): Response<Unit>

    @DELETE("itinerary/detail/delete/{detailId}")
    suspend fun deleteItineraryItem(
        @Path("detailId") id: Long
    ): Response<Unit>

    @POST("itinerary/create")
    suspend fun createItinerary(
        @Header("user-email") email: String,
        @Body itinerary: Itinerary
    ): Response<Unit>

    @POST("auth/login")
    suspend fun login(
        @Body credentials: Map<String, String>
    ): Response<LoginResponse>
}
