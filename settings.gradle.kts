pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    plugins {
        // TODO Kotlin 1.8.20 -> https://github.com/JetBrains/compose-multiplatform/tree/master/examples/falling-balls
        kotlin("jvm") version "1.8.10"
        kotlin("multiplatform") version "1.8.10"
        kotlin("plugin.serialization") version "1.8.10"
        kotlin("plugin.spring") version "1.8.10"
        kotlin("android") version "1.8.10"
        kotlin("kapt") version "1.8.10"

        id("org.springframework.boot") version "3.0.5"
        id("org.graalvm.buildtools.native") version "0.9.20"

        id("com.android.base") version "7.4.1"
        id("com.android.application") version "7.4.1"
        id("com.android.library") version "7.4.1"

        id("org.jetbrains.compose") version "1.3.1"

        id("com.squareup.anvil") version "2.4.4"
        id("app.cash.molecule") version "0.8.0"
    }
}

rootProject.name = "elevated"

include("common:client")
include("common:model")
include("frontend:android")
include("frontend:state")
include("frontend:webapp")
include("iot:raspberry")
include("service")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
