import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "1.9.23"
}

group = "me.manus.mmostats"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io") // For MMOItems & HeadDatabase
}

dependencies {
    // Paper API for Minecraft 1.20.6
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")

    // Adventure API for modern text formatting (included in Paper, but good for compilation)
    compileOnly("net.kyori:adventure-api:4.17.0")
    
    // MiniMessage for parsing string formats (shaded)
    implementation("net.kyori:adventure-text-minimessage:4.17.0")

    // Soft Dependencies
    compileOnly("net.Indyuce:mmoitems-api:6.10.4") // Use the API version
    compileOnly("com.github.arcaniax:HeadDatabase-API:v1.3.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("MMOStats")
    archiveClassifier.set("") // No classifier, e.g., MMOStats-1.0.0.jar
    archiveVersion.set(project.version.toString())

    // Relocate MiniMessage to avoid conflicts with other plugins
    relocate("net.kyori.adventure.text.minimessage", "me.manus.mmostats.libs.minimessage")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
