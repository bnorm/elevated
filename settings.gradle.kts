pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version "2.0.21"
        kotlin("multiplatform") version "2.0.21"
        kotlin("plugin.serialization") version "2.0.21"
        kotlin("plugin.spring") version "2.0.21"
        kotlin("plugin.compose") version "2.0.21"
        kotlin("android") version "2.0.21"
        kotlin("kapt") version "2.0.21"

        id("org.springframework.boot") version "3.2.2"

        id("com.android.base") version "8.5.0"
        id("com.android.application") version "8.5.0"
        id("com.android.library") version "8.5.0"

        id("org.jetbrains.compose") version "1.6.11"

        id("com.squareup.anvil") version "2.5.0-beta11"
        id("app.cash.molecule") version "2.0.0"
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
