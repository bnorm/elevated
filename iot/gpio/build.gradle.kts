plugins {
    kotlin("multiplatform")
}

kotlin {
    linuxArm64 {
        val main by compilations.getting {
            cinterops.create("gpiod") {
                includeDirs("src/nativeInterop/cinterop/headers/include/")
            }

            cinterops.create("i2c") {
                includeDirs("src/nativeInterop/cinterop/headers/include/")
            }

            cinterops.create("spi") {
                includeDirs("src/nativeInterop/cinterop/headers/include/")
            }
        }
    }

    sourceSets {
        val linuxArm64Main by getting {
            languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
    }
}
