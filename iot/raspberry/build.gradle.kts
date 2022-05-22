plugins {
    kotlin("jvm")
    application
}

dependencies {
    implementation(platform(project(":platform")))

    implementation(project(":common:client"))

    implementation("org.slf4j:slf4j-simple:1.7.36")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

    implementation("com.pi4j:pi4j-core:2.1.1")
    implementation("com.pi4j:pi4j-plugin-raspberrypi:2.1.1")
    implementation("com.pi4j:pi4j-plugin-pigpio:2.1.1")
    implementation("com.pi4j:pi4j-plugin-linuxfs:2.1.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
    mainClass.set("dev.bnorm.elevated.raspberry.MainKt")
}
