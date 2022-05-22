package dev.bnorm.elevated.client

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create

fun createElevatedClient(store: TokenStore): ElevatedClient {
    val contentType: MediaType = MediaType.get("application/json")

    val httpClient = OkHttpClient.Builder()
        .addInterceptor {
            val authorization = store.authorization
            val response = it.proceed(
                if (authorization != null) {
                    it.request()
                        .newBuilder()
                        .header("Authorization", authorization)
                        .build()
                } else {
                    it.request()
                }
            )

            if (response.code() == 401) {
                store.authorization = null
            }

            response
        }
        .build()

    val retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl(HttpUrl.get("https://elevated.bnorm.dev"))
        .addConverterFactory(Json.asConverterFactory(contentType))
        .build()


    return retrofit.create()
}
