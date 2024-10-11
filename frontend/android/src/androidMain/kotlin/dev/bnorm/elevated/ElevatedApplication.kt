package dev.bnorm.elevated

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dev.bnorm.elevated.di.DaggerElevatedApplicationComponent
import dev.bnorm.elevated.di.HttpSecurityModule
import dev.bnorm.elevated.state.SharedPreferenceTokenStore
import dev.bnorm.elevated.work.ElevatedWorkerFactory
import javax.inject.Inject

class ElevatedApplication : Application(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var workerFactory: ElevatedWorkerFactory

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        val tokenStore = SharedPreferenceTokenStore(getSharedPreferences("KEYS", Context.MODE_PRIVATE))
        val component = DaggerElevatedApplicationComponent.builder()
            .httpSecurityModule(HttpSecurityModule(tokenStore))
            .build()
        component.inject(this)

        super.onCreate()

        val workManagerConfiguration = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfiguration)
    }
}
