pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version "1.9.22"
        kotlin("multiplatform") version "1.9.22"
        kotlin("plugin.serialization") version "1.9.22"
        kotlin("plugin.spring") version "1.9.22"
        kotlin("android") version "1.9.22"
        kotlin("kapt") version "1.9.22"

        id("org.springframework.boot") version "3.2.2"

        id("com.android.base") version "8.2.2"
        id("com.android.application") version "8.2.2"
        id("com.android.library") version "8.2.2"

        id("org.jetbrains.compose") version "1.5.12"

        id("com.squareup.anvil") version "2.4.9"
        id("app.cash.molecule") version "1.3.2"
    }
}

rootProject.name = "elevated"

include("common:client")
include("common:model")
include("frontend:android")
include("frontend:state")
include("frontend:webapp")
include("iot:gpio")
include("iot:raspberry")
include("service")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
