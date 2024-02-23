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

                api(libs.molecule.runtime)
            }
        }
        named("androidMain") {
            dependencies {
                implementation(libs.javax.inject)

                implementation(libs.androidx.lifecycle.runtime.ktx)
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
