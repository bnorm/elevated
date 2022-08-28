import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times in each subproject's classloader
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
    kotlin("kapt") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
    id("com.squareup.anvil") apply false
}

allprojects {
    group = "com.bnorm.elevated"

    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
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
