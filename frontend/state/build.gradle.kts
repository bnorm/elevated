import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.metro)
}

kotlin {
    androidTarget()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:client"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material)

                implementation("org.jetbrains.compose.material:material-icons-core:1.7.3")

                api(libs.molecule.runtime)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.lifecycle.runtime.ktx)
            }
        }
    }
}

android {
    compileSdk = 35

    namespace = "dev.bnorm.elevated.state"
    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
