package dev.bnorm.elevated.di

import com.squareup.anvil.annotations.MergeComponent
import dagger.android.AndroidInjectionModule
import dev.bnorm.elevated.ElevatedApplication
import javax.inject.Singleton

@Singleton
@MergeComponent(SecurityScope::class)
@MergeComponent(HttpScope::class)
@MergeComponent(ClientScope::class)
@MergeComponent(WorkerScope::class)
@MergeComponent(ActivityScope::class, modules = [AndroidInjectionModule::class])
interface ElevatedApplicationComponent {
    fun inject(application: ElevatedApplication)
}
