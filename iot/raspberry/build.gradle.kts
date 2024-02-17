plugins {
    kotlin("multiplatform")
    application
}

kotlin {
    jvm {
        // Needed for integration with application plugin
        withJava()
    }
    linuxArm64 {
        // TODO enable binary building
        // binaries {
        //     executable()
        // }
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":common:client"))
            }
        }
        named("jvmMain") {
            dependencies {
                implementation(libs.slf4j.simple)

                implementation(libs.kotlinx.coroutines.jdk8)

                implementation(libs.bundles.pi4j.raspberrypi)
            }
        }
        named("jvmTest") {
            dependencies {
                implementation(libs.junit.jupiter.api)
                runtimeOnly(libs.junit.jupiter.engine)
            }
        }
    }
}

application {
    mainClass.set("MainKt")
}
