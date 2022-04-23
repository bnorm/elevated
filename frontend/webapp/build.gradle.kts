import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.1.0"
}

kotlin {
    js(IR) {
        browser {
            runTask {
                // TODO: use dsl after KT-32016 will be fixed
                devServer = KotlinWebpackConfig.DevServer(
                    port = 8081,
                    proxy = mutableMapOf("/api/v1/**" to "http://localhost:8080"),
                    static = mutableListOf("$buildDir/processedResources/js/main")
                )
            }
        }
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":common:model"))

                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
    }
}

// TODO work around until Kotlin 1.6.20
rootProject.plugins.withType<NodeJsRootPlugin> {
    rootProject.configure<NodeJsRootExtension> {
        nodeVersion = "16.14.2"
    }
}
