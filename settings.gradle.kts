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
include("frontend:components")
include("frontend:platforms:android")
include("frontend:platforms:desktop")
include("frontend:platforms:web")
include("frontend:state")
include("iot:gpio")
include("iot:raspberry")
include("service")
