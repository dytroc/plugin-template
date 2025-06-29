import io.papermc.paperweight.util.capitalized
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.resourcefactory)
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight)
    alias(libs.plugins.runpaper)
}

version = "1.0.0"
description = "Plugin template"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    paperweight.paperDevBundle(libs.versions.paper)

//    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:latest.release")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    compileJava {
        options.release.set(21)
    }

    jar {
        archiveBaseName.set(rootProject.name)
        archiveVersion.set(version.toString())
        archiveClassifier.set("")
    }

    runServer {
        minecraftVersion(libs.versions.paper.get().takeWhile { it != '-' })
        jvmArgs = listOf("-Dcom.mojang.eula.agree=true")
    }
}

bukkitPluginYaml {
    val pluginName = project.name.split('-').joinToString("") { it.capitalized() }
    name = pluginName

    authors = listOf("dytroc")

    val packageName = "io.github.dytroc.${project.name.split('-').joinToString("").lowercase()}"

    apiVersion = libs.versions.paper.orNull?.split("-")?.firstOrNull() ?: "1.21.6"
    main = "$packageName.${pluginName}Plugin"
}
