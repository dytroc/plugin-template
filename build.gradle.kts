import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val pluginVersion = "1.0.0"

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    paperweight.paperDevBundle(libs.versions.paper)

    implementation("io.github.monun:kommand-api:3.1.7")
//    implementation("io.github.monun:tap-api:latest.release")
//    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:latest.release")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }

    jar {
        archiveBaseName.set(rootProject.name)
        archiveVersion.set(pluginVersion)
        archiveClassifier.set("")
    }

    register<Copy>("buildDevJar") {
        from(jar)
        into(rootProject.file(".server/plugins"))
    }
}

bukkit {
    name = project.name.split('-').joinToString("") { it.capitalized() }
    version = pluginVersion

    description = "Random plugin"

    authors = listOf("dytroc")

    apiVersion = libs.versions.paper.orNull?.split(".")?.take(2)?.joinToString(".") ?: "1.20"
    main = "io.github.dytroc.${project.name.split('-').joinToString("").lowercase()}.${name}Plugin"

    // Taken from https://github.com/monun/paper-sample/blob/master/build.gradle.kts#L49-L63
    libraries = configurations.findByName("implementation")?.allDependencies?.map {
        val group = it.group ?: error("group is null")
        var name = it.name ?: error("name is null")
        var version = it.version

        if (group == "org.jetbrains.kotlin" && version == null) {
            version = getKotlinPluginVersion()
        } else if (name.endsWith("-api")) {
            name = name.removeSuffix("api") + "core"
        }

        requireNotNull(version) { "version is null" }
        require(version != "latest.release") { "version is latest.release" }

        "$group:$name:$version"
    } ?: emptyList()
}