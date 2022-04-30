plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.1"))
    api(platform("org.springframework.boot:spring-boot-dependencies:2.6.6"))
    api(platform("org.testcontainers:testcontainers-bom:1.17.1"))
}
