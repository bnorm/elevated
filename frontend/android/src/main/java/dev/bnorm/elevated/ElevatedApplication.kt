package dev.bnorm.elevated

import android.app.Application
import android.content.Context
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dev.bnorm.elevated.di.DaggerElevatedApplicationComponent
import dev.bnorm.elevated.state.SharedPreferenceTokenStore
import javax.inject.Inject

class ElevatedApplication : Application(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()

        val tokenStore = SharedPreferenceTokenStore(getSharedPreferences("KEYS", Context.MODE_PRIVATE))

        DaggerElevatedApplicationComponent.builder()
            .tokenStore(tokenStore)
            .build()
            .inject(this)
    }
}
