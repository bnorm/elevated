pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
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
