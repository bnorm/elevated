@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.compose")
    kotlin("android")
    kotlin("kapt")
    id("com.squareup.anvil")
}

android {
    compileSdk = 33
    buildToolsVersion = "33.0.1"

    namespace = "dev.bnorm.elevated"
    defaultConfig {
        applicationId = "dev.bnorm.elevated"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }

    packagingOptions {
        resources.excludes += "**/attach_hotspot_windows.dll"
        resources.excludes += "META-INF/licenses/**"
        resources.excludes += "META-INF/AL2.0"
        resources.excludes += "META-INF/LGPL2.1"
    }

    lint {
        abortOnError = false
    }
}

dependencies {
    implementation(project(":common:client"))
    implementation(project(":frontend:state"))

    implementation("com.google.dagger:dagger:2.45")
    implementation("com.google.dagger:dagger-android:2.45")
    implementation("com.google.dagger:dagger-android-support:2.45")
    kapt("com.google.dagger:dagger-compiler:2.45")

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")

    implementation("androidx.compose.ui:ui:1.4.2")
    implementation("androidx.compose.ui:ui-tooling:1.4.2")
    implementation("androidx.compose.material:material:1.4.2")
    implementation("androidx.compose.material:material-icons-extended:1.4.2")
    implementation("androidx.navigation:navigation-compose:2.5.3")

    implementation("com.google.accompanist:accompanist-swiperefresh:0.30.1")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.1")

    implementation("androidx.work:work-runtime-ktx:2.8.1")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.2")
}
