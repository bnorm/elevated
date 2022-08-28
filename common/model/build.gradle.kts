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
                implementation(project.dependencies.platform(project(":platform")))

                api("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
            }
        }
    }
}
