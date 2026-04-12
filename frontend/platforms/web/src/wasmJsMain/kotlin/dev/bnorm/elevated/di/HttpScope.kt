package dev.bnorm.elevated.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

@ContributesTo(AppScope::class)
interface HttpModule {
    @SingleIn(AppScope::class)
    @Provides
    fun httpClient(): HttpClient {
        return HttpClient(Js)
    }
}
