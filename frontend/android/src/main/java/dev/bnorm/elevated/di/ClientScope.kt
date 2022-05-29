package dev.bnorm.elevated.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dev.bnorm.elevated.client.ElevatedClient
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

abstract class ClientScope private constructor()

@Module
@ContributesTo(ClientScope::class)
class ClientModule {
    @Singleton
    @Provides
    fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(HttpUrl.get("https://elevated.bnorm.dev"))
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .build()
    }

    @Singleton
    @Provides
    fun elevatedClient(retrofit: Retrofit): ElevatedClient = retrofit.create()
}
