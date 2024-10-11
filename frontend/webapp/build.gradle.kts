import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser {
            commonWebpackConfig {
                val env: String? by project
                val proxy = when (env) {
                    "dev" -> mutableListOf(
                        KotlinWebpackConfig.DevServer.Proxy(
                            context = mutableListOf("/api/v1/devices/[a-zA-Z0-9]+/connect"),
                            target = "ws://localhost:8080",
//                            "ws" to true
                        ),
                        KotlinWebpackConfig.DevServer.Proxy(
                            context = mutableListOf("/api/**"),
                            target = "http://localhost:8080",
                        )
                    )

                    else -> mutableListOf(
                        KotlinWebpackConfig.DevServer.Proxy(
                            context = mutableListOf("/api/v1/devices/[a-zA-Z0-9]+/connect"),
                            target = "wss://elevated.bnorm.dev",
                            changeOrigin = true,
//                            "ws" to true
                        ),
                        KotlinWebpackConfig.DevServer.Proxy(
                            context = mutableListOf("/api/**"),
                            target = "https://elevated.bnorm.dev",
                            changeOrigin = true,
                        )
                    )

                }

                // TODO: use dsl after KT-32016 will be fixed
                devServer = devServer?.copy(
                    proxy = (devServer?.proxy.orEmpty() + proxy).toMutableList(),
                )
            }
        }
    }
    sourceSets {
        wasmJsMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material)

                implementation(project(":common:client"))
                implementation(project(":frontend:state"))
            }
        }
    }
}
