import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
//    id("org.graalvm.buildtools.native")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(platform(libs.kotlinx.coroutines.bom))

    implementation(project(":common:model"))

    implementation(libs.kotlinx.coroutines.slf4j)
    implementation(libs.kotlinx.coroutines.reactor)
//    implementation("io.projectreactor:reactor-tools")

    implementation(libs.kotlinx.serialization.json)
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.apache.kafka:kafka-streams")

    testImplementation(platform(libs.junit.bom))
    testImplementation(platform(libs.testcontainers.bom))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:mongodb")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.processResources.configure {
    from(tasks.getByPath(":frontend:webapp:jsBrowserDistribution")) {
        into("static")
    }
}
