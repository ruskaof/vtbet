plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
}

group = "ru.itmo.bets.handler"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":vtbet-common"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.postgresql:postgresql")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.0")
    implementation("org.springframework.security:spring-security-oauth2-jose:6.1.1")
    implementation("org.springframework.security:spring-security-core:6.1.1")
    implementation("org.springframework.security:spring-security-web:6.1.1")
    implementation("org.springframework.security:spring-security-config:6.1.1")
    implementation("org.springframework.security:spring-security-oauth2-resource-server:6.1.1")
    implementation("org.springframework.security:spring-websocket:6.1.1")

    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-api
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl
    implementation("io.jsonwebtoken:jjwt-impl:0.12.5")
    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-jackson
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.5")

    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.3")
    implementation("org.springframework.cloud:spring-cloud-starter-config:4.1.3")

    testImplementation("org.testcontainers:junit-jupiter:1.19.8")
    testImplementation("org.testcontainers:postgresql:1.19.8")
    testImplementation("org.liquibase:liquibase-core")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
