plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api(project(":common:model"))
            }
        }
        named("jsMain") {
            dependencies {
                val ktorVersion = "1.6.8"
                api("io.ktor:ktor-client-js:$ktorVersion")
                api("io.ktor:ktor-client-serialization:$ktorVersion")
                api("io.ktor:ktor-client-websockets:$ktorVersion")
            }
        }
        named("jvmMain") {
            dependencies {
                api("com.squareup.retrofit2:retrofit:2.9.0")
                api("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
            }
        }
    }
}
