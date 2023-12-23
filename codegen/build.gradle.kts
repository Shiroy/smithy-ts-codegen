import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
}

repositories {
    mavenCentral()
}

group = "com.awacheux.smithy"
version= "1.0-SNAPSHOT"

dependencies {
    val smithyVersion = "1.42.0"
    api("software.amazon.smithy:smithy-codegen-core:$smithyVersion")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<JavaCompile>() {
    targetCompatibility = "1.8"
}