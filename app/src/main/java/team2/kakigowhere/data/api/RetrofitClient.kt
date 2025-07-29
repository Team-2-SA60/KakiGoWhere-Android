package team2.kakigowhere.data.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val gson = GsonBuilder()
        .registerTypeAdapter(ByteArray::class.java, Base64Deserializer())
        .create()

    val api: ApiService by lazy {
        Retrofit
            .Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
