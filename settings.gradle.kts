pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    plugins {
        kotlin("jvm") version "1.8.20"
        kotlin("multiplatform") version "1.8.20"
        kotlin("plugin.serialization") version "1.8.20"
        kotlin("plugin.spring") version "1.8.20"
        kotlin("android") version "1.8.20"
        kotlin("kapt") version "1.8.20"

        id("org.springframework.boot") version "3.0.5"
        id("org.graalvm.buildtools.native") version "0.9.21"

        id("com.android.base") version "7.4.1"
        id("com.android.application") version "7.4.1"
        id("com.android.library") version "7.4.1"

        id("org.jetbrains.compose") version "1.4.0"

        id("com.squareup.anvil") version "2.4.5"
        id("app.cash.molecule") version "0.9.0"
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
