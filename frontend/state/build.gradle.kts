@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("app.cash.molecule")
}

kotlin {
    android()
    js(IR) {
        nodejs()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":common:client"))
                api(compose.runtime)

                api("app.cash.molecule:molecule-runtime:0.4.0")
            }
        }
        named("androidMain") {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)

                implementation("javax.inject:javax.inject:1")
            }
        }
        named("jsMain") {
            dependencies {
                implementation(compose.web.core)
            }
        }
    }
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 26
        targetSdk = 32
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res")
        }
    }
}
