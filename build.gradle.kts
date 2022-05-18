import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

plugins {
    id("org.springframework.boot") version "2.6.6" apply false
    kotlin("multiplatform") version "1.6.10" apply false
    kotlin("plugin.serialization") version "1.6.10" apply false
    kotlin("plugin.spring") version "1.6.10" apply false
}

group = "com.bnorm.elevated"

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    val compilerArgs = listOf(
        "-Xopt-in=kotlin.RequiresOptIn",
    )
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = freeCompilerArgs + compilerArgs
        }
    }
    tasks.withType<Kotlin2JsCompile> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + compilerArgs
        }
    }
    tasks.withType<KotlinCompileCommon> {
        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + compilerArgs
        }
    }
}

rootProject.plugins.withType<YarnPlugin> {
    rootProject.configure<YarnRootExtension> {
        lockFileDirectory = project.rootDir.resolve("gradle/kotlin-js-store")
    }
}

// TODO work around until Kotlin 1.6.20
rootProject.plugins.withType<NodeJsRootPlugin> {
    rootProject.configure<NodeJsRootExtension> {
        nodeVersion = "16.14.2"
    }
}
