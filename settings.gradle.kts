pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version "1.7.10"
        kotlin("multiplatform") version "1.7.10"
        kotlin("plugin.serialization") version "1.7.10"
        kotlin("plugin.spring") version "1.7.10"
        kotlin("android") version "1.7.10"
        kotlin("kapt") version "1.7.10"

        id("org.springframework.boot") version "2.7.2"

        id("com.android.base") version "7.2.2"
        id("com.android.application") version "7.2.2"
        id("com.android.library") version "7.2.2"

        id("org.jetbrains.compose") version "1.2.0-alpha01-dev770"

        id("com.squareup.anvil") version "2.4.2"
    }
}

rootProject.name = "elevated"

include("common:client")
include("common:model")
include("frontend:android")
include("frontend:state")
include("frontend:webapp")
include("iot:raspberry")
include("platform")
include("service")

enableFeaturePreview("VERSION_CATALOGS")
