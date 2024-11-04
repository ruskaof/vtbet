plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
}

group = "ru.itmo.eureka"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-dependencies
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")

    }
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server:4.1.3")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}