plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.compose)
    alias(libs.plugins.metro)
}

kotlin {
    jvm()

    sourceSets {
        jvmMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material)
                implementation(compose.desktop.currentOs)

                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.androidx.navigation.compose)
                implementation(libs.jetbrains.compose.material.icons.core)

                implementation(project(":common:client"))
                implementation(project(":frontend:components"))
            }
        }
    }
}
