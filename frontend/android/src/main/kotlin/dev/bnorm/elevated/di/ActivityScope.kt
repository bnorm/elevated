package dev.bnorm.elevated.di

import android.app.Activity
import dev.bnorm.elevated.MainActivity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.MembersInjector

@ContributesTo(AppScope::class)
interface MainActivityModule {
    @Binds
    @IntoMap
    @ClassKey(MainActivity::class)
    fun bindYourAndroidInjectorFactory(factory: MembersInjector<MainActivity>): MembersInjector<Activity>
}
