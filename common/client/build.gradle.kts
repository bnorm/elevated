plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }
    linuxArm64()

    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":common:model"))

                api(libs.ktor.client.core)
                api(libs.ktor.client.websockets)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)
            }
        }
        named("jsMain") {
            dependencies {
                api(libs.ktor.client.js)
            }
        }
        named("jvmMain") {
            dependencies {
                api(libs.ktor.client.okhttp)
            }
        }
        named("linuxArm64Main") {
            dependencies {
                api(libs.ktor.client.cio)
            }
        }
    }
}
