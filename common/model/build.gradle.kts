plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm()
    js(IR) {
        browser()
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.serialization.json)
            }
        }
    }
}
