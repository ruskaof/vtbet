plugins {
    kotlin("jvm") version "1.9.25"
}

group = "ru.itmo.common"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.3")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
