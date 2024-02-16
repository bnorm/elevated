plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
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
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.serialization.json)
            }
        }
    }
}
