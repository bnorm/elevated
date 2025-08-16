import org.gradle.kotlin.dsl.mavenCentral
import org.gradle.kotlin.dsl.repositories

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
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
