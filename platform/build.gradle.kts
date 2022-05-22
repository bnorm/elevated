plugins {
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.1"))
    api(platform("org.springframework.boot:spring-boot-dependencies:2.6.6"))
    api(platform("io.ktor:ktor-bom:1.6.8"))
    api(platform("org.junit:junit-bom:5.8.2"))
    api(platform("org.testcontainers:testcontainers-bom:1.17.1"))
}
