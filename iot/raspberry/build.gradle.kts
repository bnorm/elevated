apply("$rootDir/gradle/native-libs.gradle.kts")

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
        binaries {
            executable {
                linkTask.dependsOn(tasks.named("nativeLibs"))
                linkerOpts.add("-L$buildDir/native/libs/usr/lib/aarch64-linux-gnu/")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common:client"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.slf4j.simple)

                implementation(libs.kotlinx.coroutines.jdk8)

                implementation(libs.bundles.pi4j.raspberrypi)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.junit.jupiter.api)
                runtimeOnly(libs.junit.jupiter.engine)
            }
        }
        val linuxArm64Main by getting {
            dependencies {
                implementation(project(":iot:gpio"))
            }
        }
    }
}

application {
    mainClass.set("MainKt")
}
