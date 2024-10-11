@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    kotlin("kapt")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("com.squareup.anvil")
}

kotlin {
    androidTarget()
    sourceSets {
        androidMain {
            dependencies {
                implementation(project(":common:client"))
                implementation(project(":frontend:state"))

                implementation(libs.bundles.androidx.compose)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.navigation.compose)

                implementation(libs.google.android.material)

                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.androidx.work.runtime.ktx)

                implementation(libs.bundles.dagger.android)
                configurations.get("kapt").dependencies.add(libs.dagger.compiler.get())
            }
        }
    }
}

android {
    compileSdk = 35
    namespace = "dev.bnorm.elevated"
    defaultConfig {
        applicationId = "dev.bnorm.elevated"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}
