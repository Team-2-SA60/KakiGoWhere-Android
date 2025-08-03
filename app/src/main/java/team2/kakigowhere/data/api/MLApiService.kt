package team2.kakigowhere.data.api

import retrofit2.http.Body
import retrofit2.http.POST

data class RecommendRequest(val interests: List<String>)
data class RecommendResponse(val id: Long, val name: String)

interface MLApiService {
    @POST ("recommend")
    suspend fun getRecommendations(@Body request: RecommendRequest): List<RecommendResponse>
}