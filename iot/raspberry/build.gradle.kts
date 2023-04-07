plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(project(":common:client"))

    implementation(libs.slf4j.simple)

    implementation(libs.kotlinx.coroutines.jdk8)

    implementation(libs.bundles.pi4j.raspberrypi)

    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

application {
    mainClass.set("dev.bnorm.elevated.raspberry.MainKt")
}
