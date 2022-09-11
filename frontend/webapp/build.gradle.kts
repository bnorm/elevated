plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        binaries.executable()
        useCommonJs()
        browser {
            commonWebpackConfig {
                sourceMaps = true
                cssSupport.enabled = true

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
                implementation(compose.web.core)
                implementation(compose.runtime)

                implementation(project(":common:client"))
                implementation(project(":frontend:state"))

                implementation("dev.petuska:kmdc-button:0.0.5")
                implementation("dev.petuska:kmdc-dialog:0.0.5")
                implementation("dev.petuska:kmdc-layout-grid:0.0.5")
                implementation("dev.petuska:kmdc-tab-bar:0.0.5")
                implementation("dev.petuska:kmdc-textfield:0.0.5")

                // SCSS dependencies
                implementation(devNpm("style-loader", "^3.3.1"))
                implementation(devNpm("css-loader", "^6.7.1"))
                implementation(devNpm("sass-loader", "^13.0.0"))
                implementation(devNpm("sass", "^1.52.1"))
            }
        }
    }
}
