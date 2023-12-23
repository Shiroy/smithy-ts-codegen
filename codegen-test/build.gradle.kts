plugins {
    id("software.amazon.smithy") version "0.6.0"
}

buildscript {
    val smithyVersion = "1.42.0"
    dependencies {
        classpath("software.amazon.smithy:smithy-model:$smithyVersion")
        classpath("software.amazon.smithy:smithy-aws-traits:$smithyVersion")
    }
}

group = "com.awacheux.smithy"
version= "1.0-SNAPSHOT"

tasks["jar"].enabled = false

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":codegen"))
}

java.sourceSets["main"].java {
    srcDirs("model", "src/main/smithy")
}
