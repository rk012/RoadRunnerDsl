import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
}

group = "io.github.rk012"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()

    maven("https://maven.brott.dev/")
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.acmerobotics.roadrunner:core:0.5.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}