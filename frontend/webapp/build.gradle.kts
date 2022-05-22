import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
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
                val proxy: MutableMap<String, Any> = when (env) {
                    "prod" -> mutableMapOf(
                        "/api/v1/devices/[a-zA-Z0-9]+/connect" to mapOf(
                            "target" to "wss://elevated.bnorm.dev",
                            "changeOrigin" to true,
                            "ws" to true
                        ),
                        "/api/**" to mapOf(
                            "target" to "https://elevated.bnorm.dev",
                            "changeOrigin" to true,
                        )
                    )
                    else -> mutableMapOf(
                        "/api/v1/devices/[a-zA-Z0-9]+/connect" to mapOf(
                            "target" to "ws://localhost:8080",
                            "ws" to true
                        ),
                        "/api/**" to "http://localhost:8080",
                    )
                }
                devServer = KotlinWebpackConfig.DevServer(
                    port = 8081,
                    proxy = proxy,
                    static = mutableListOf("$buildDir/processedResources/js/main")
                )
            }
        }
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":common:client"))

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
