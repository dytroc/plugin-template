import io.papermc.paperweight.util.path
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files

val pluginVersion = "1.0.0"

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight)
    alias(libs.plugins.runpaper)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    paperweight.paperDevBundle(libs.versions.paper)

//    implementation("io.github.monun:kommand-api:latest.release")
//    implementation("io.github.monun:tap-api:latest.release")
//    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:latest.release")
}

val librariesData: Provider<Directory> = layout.buildDirectory.dir("generated/libraries-data").also {
    Files.createDirectories(it.get().path)
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

    runServer {
        minecraftVersion(libs.versions.paper.get().takeWhile { it != '-' })
        jvmArgs = listOf("-Dcom.mojang.eula.agree=true")
    }
}

paper {
    name = project.name.split('-').joinToString("") { it.capitalized() }
    version = pluginVersion

    description = "Random plugin"

    authors = listOf("dytroc")

    val packageName = "io.github.dytroc.${project.name.split('-').joinToString("").lowercase()}"

    apiVersion = libs.versions.paper.orNull?.split(".")?.take(2)?.joinToString(".") ?: "1.20"
    main = "$packageName.${name}Plugin"
    loader = "$packageName.${name}PluginLoader"

    foliaSupported = false
}

plugins.withType<JavaPlugin> {
    librariesData.orNull?.file("maven.libraries")?.asFile?.apply {
        // Taken from https://github.com/monun/paper-sample/blob/master/build.gradle.kts#L49-L63
        writeText(
            configurations.findByName("implementation")?.allDependencies?.joinToString("\n") {
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
            } ?: ""
        )
    }?.let {
        extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
            resources.srcDir(librariesData.path)
        }
    }
}