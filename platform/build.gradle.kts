plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4"))
    api(platform("org.springframework.boot:spring-boot-dependencies:3.0.0"))
    api(platform("io.ktor:ktor-bom:2.1.3"))
    api(platform("org.junit:junit-bom:5.9.0"))
    api(platform("org.testcontainers:testcontainers-bom:1.17.6"))
}
