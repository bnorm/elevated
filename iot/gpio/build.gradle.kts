plugins {
    alias(libs.plugins.kotlin.multiplatform)
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
        linuxArm64Main {
            languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
    }
}
