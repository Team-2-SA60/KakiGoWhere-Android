package team2.kakigowhere.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object MLRetrofitClient {
    val api: MLApiService by lazy {
        Retrofit
            .Builder()
            .baseUrl(ApiConstants.ML_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MLApiService::class.java)
    }
}
