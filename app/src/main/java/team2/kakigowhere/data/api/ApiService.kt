package team2.kakigowhere.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import team2.kakigowhere.data.model.Place
import team2.kakigowhere.data.model.PlaceDTO
import team2.kakigowhere.data.model.RatingItem
import team2.kakigowhere.data.model.RatingRequest
import team2.kakigowhere.data.model.RatingSummary

interface ApiService {
    @GET("places")
    suspend fun getPlaces(): Response<List<Place>>

    @GET("places/id/{placeId}")
    suspend fun getPlaceDetail(@Path("placeId") placeId: Long): Response<PlaceDTO>

    @GET("ratings/{placeId}/summary")
    suspend fun getRatingSummary(@Path("placeId") placeId: Long): Response<RatingSummary>

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
}
