import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.6" apply false
    kotlin("jvm") version "1.6.20" apply false
    kotlin("plugin.serialization") version "1.6.20" apply false
    kotlin("plugin.spring") version "1.6.20" apply false
}

group = "com.bnorm.elevated"

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
        }
    }
}
