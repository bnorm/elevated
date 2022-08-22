plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        val ktorVersion = "1.6.8"

        named("commonMain") {
            dependencies {
                api(project(":common:model"))

                api("io.ktor:ktor-client-serialization:$ktorVersion")
                api("io.ktor:ktor-client-websockets:$ktorVersion")
            }
        }
        named("jsMain") {
            dependencies {
                api("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
        named("jvmMain") {
            dependencies {
                api("io.ktor:ktor-client-okhttp:$ktorVersion")

                api("com.squareup.retrofit2:retrofit:2.9.0")
                api("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
            }
        }
    }
}
