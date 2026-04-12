import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.metro)
}

kotlin {
    jvm()

    androidTarget()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
        optIn.add("kotlinx.coroutines.FlowPreview")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:client"))
                api(project(":frontend:state"))

                api(libs.molecule.runtime)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material)

                implementation(libs.androidx.navigation.compose)
                implementation(libs.jetbrains.compose.material.icons.core)
            }
        }
        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
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
    compileSdk = 36

    namespace = "dev.bnorm.elevated.components"
    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
