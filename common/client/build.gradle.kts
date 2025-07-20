import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    linuxArm64()

    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":common:model"))

                api(dependencies.platform(libs.ktor.bom))
                api(libs.ktor.client.core)
                api(libs.ktor.client.websockets)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)
            }
        }
        wasmJsMain {
            dependencies {
                api(libs.ktor.client.js)
                implementation(libs.kotlinx.browser)
            }
        }
        jvmMain {
            dependencies {
                api(libs.ktor.client.okhttp)
            }
        }
        linuxArm64Main {
            dependencies {
                api(libs.ktor.client.cio)
            }
        }
    }
}
