import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    // beware to not silently import kotlin stdlib code into java
    // kotlin("jvm") version "1.4.31"
    kotlin("jvm") version "1.4.21"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // implementation(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
        // languageVersion = "1.5"
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("robotSpaceExplorator")
        mergeServiceFiles()
        isZip64 = true // needed for hive-jdbc, do not enable if not needed
        manifest {
            attributes(mapOf("Main-Class" to "RobotSpaceExploratorKt"))
        }
    }
}
tasks {
    build {
        dependsOn(shadowJar)
    }
}
