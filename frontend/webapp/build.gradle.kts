plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                sourceMaps = true
                cssSupport { enabled.set(true) }
                scssSupport { enabled.set(true) }

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

                // TODO: use dsl after KT-32016 will be fixed
                devServer = devServer?.copy(
                    port = 8081,
                    proxy = proxy,
                    static = mutableListOf("$buildDir/processedResources/js/main")
                )
            }
        }
    }
    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)

                implementation(project(":common:client"))
                implementation(project(":frontend:state"))
            }
        }
    }
}

compose.experimental {
    web.application {}
}
