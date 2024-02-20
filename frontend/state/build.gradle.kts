@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    androidTarget()
    js(IR) {
        browser()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":common:client"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material)

                api("app.cash.molecule:molecule-runtime:1.3.2")
            }
        }
        named("androidMain") {
            dependencies {
                implementation("javax.inject:javax.inject:1")

                implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
            }
        }
        named("jsMain") {
        }
    }
}

android {
    compileSdk = 34

    namespace = "dev.bnorm.elevated.state"
    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
