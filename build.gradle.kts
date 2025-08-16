import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times in each subproject's classloader
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose) apply false
}

allprojects {
    group = "dev.bnorm.elevated"

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
}

rootProject.plugins.withType<YarnPlugin> {
    rootProject.configure<YarnRootExtension> {
        lockFileDirectory = project.rootDir.resolve("gradle/kotlin-js-store")
    }
}

rootProject.plugins.withType<WasmYarnPlugin> {
    rootProject.configure<WasmYarnRootExtension> {
        lockFileDirectory = project.rootDir.resolve("gradle/kotlin-js-store/wasm")
    }
}
