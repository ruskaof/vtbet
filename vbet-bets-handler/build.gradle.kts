plugins {
    kotlin("jvm") version "1.9.25"
}

group = "ru.itmo.bets.handler"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}