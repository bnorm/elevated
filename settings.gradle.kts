pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }

    plugins {
        kotlin("jvm") version "1.6.10"
        kotlin("multiplatform") version "1.6.10"
        kotlin("plugin.serialization") version "1.6.10"
        kotlin("plugin.spring") version "1.6.10"
        kotlin("android") version "1.6.10"
        kotlin("kapt") version "1.6.10"

        id("org.springframework.boot") version "2.6.6"

        id("com.android.base") version "7.0.4"
        id("com.android.application") version "7.0.4"
        id("com.android.library") version "7.0.4"

        id("org.jetbrains.compose") version "1.1.0"

        id("com.squareup.anvil") version "2.4.0"
    }
}

rootProject.name = "elevated"

include("common:client")
include("common:model")
include("frontend:android")
include("frontend:webapp")
include("iot:raspberry")
include("platform")
include("service")

enableFeaturePreview("VERSION_CATALOGS")
