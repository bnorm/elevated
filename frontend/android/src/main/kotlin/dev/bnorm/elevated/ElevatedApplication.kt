package dev.bnorm.elevated

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dev.bnorm.elevated.di.ElevatedApplicationComponent
import dev.bnorm.elevated.state.SharedPreferenceTokenStore
import dev.bnorm.elevated.work.ElevatedWorkerFactory
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.MembersInjector
import dev.zacsweers.metro.createGraphFactory
import kotlin.reflect.KClass

class ElevatedApplication : Application() {
    @Inject
    private lateinit var workerFactory: ElevatedWorkerFactory

    @Inject
    private lateinit var activityInjectors: Map<KClass<*>, MembersInjector<Activity>>

    fun inject(activity: Activity) {
        activityInjectors.getValue(activity::class).injectMembers(activity)
    }

    override fun onCreate() {
        val tokenStore = SharedPreferenceTokenStore(getSharedPreferences("KEYS", Context.MODE_PRIVATE))
        val component = createGraphFactory<ElevatedApplicationComponent.Factory>()
            .create(tokenStore)
        component.inject(this)

        super.onCreate()

        val workManagerConfiguration = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfiguration)
    }
}
