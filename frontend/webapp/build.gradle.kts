import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.1.0"
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
            runTask {
                // TODO: use dsl after KT-32016 will be fixed
                val env: String? by project
                val proxyServer: Any = when (env) {
                    "prod" -> mapOf(
                        "target" to "https://elevated.bnorm.dev",
                        "secure" to false,
                        "changeOrigin" to true,
                    )
                    else -> "http://localhost:8080"
                }
                devServer = KotlinWebpackConfig.DevServer(
                    port = 8081,
                    proxy = mutableMapOf("/api/**" to proxyServer),
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

                val ktor_version = "1.6.8"
                implementation("io.ktor:ktor-client-js:$ktor_version")
                implementation("io.ktor:ktor-client-serialization:$ktor_version")

                val mdcVersion = "13.0.0"
                implementation(npm("@material/base", mdcVersion))
                implementation(npm("@material/ripple", mdcVersion))
                implementation(npm("@material/button", mdcVersion))
                implementation(npm("@material/icon-button", mdcVersion))
                implementation(npm("@material/textfield", mdcVersion))
                implementation(npm("@material/layout-grid", mdcVersion))
                implementation(npm("@material/dialog", mdcVersion))
                implementation(npm("@material/tab-indicator", mdcVersion))
                implementation(npm("@material/tab", mdcVersion))
                implementation(npm("@material/tab-scroller", mdcVersion))
                implementation(npm("@material/tab-bar", mdcVersion))
                implementation(npm("@material/top-app-bar", mdcVersion))

                implementation(compose.web.core)
                implementation(compose.runtime)
            }
        }
    }
}
