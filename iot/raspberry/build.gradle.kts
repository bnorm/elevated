apply("$rootDir/gradle/native-libs.gradle.kts")

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvm {
        binaries {
            executable {
                mainClass.set("MainKt")
            }
        }
    }
    linuxArm64 {
        binaries {
            executable {
                linkTaskProvider.configure { dependsOn(tasks.named("nativeLibs")) }
                linkerOpts.add("-L$buildDir/native/libs/usr/lib/aarch64-linux-gnu/")
            }
        }
    }

    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
        optIn.add("kotlinx.cinterop.ExperimentalForeignApi")
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.ExperimentalStdlibApi")
        }

        commonMain {
            dependencies {
                implementation(project(":common:client"))
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.slf4j.simple)

                implementation(libs.kotlinx.coroutines.jdk8)

                implementation(libs.bundles.pi4j.raspberrypi)
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.junit.jupiter.api)
                runtimeOnly(libs.junit.jupiter.engine)
            }
        }
        linuxArm64Main {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
            dependencies {
                implementation(project(":iot:gpio"))
            }
        }
    }
}
