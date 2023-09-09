package io.github.dytroc.plugintemplate;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import net.kyori.adventure.text.Component;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class PluginTemplatePluginLoader implements PluginLoader {
    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        var libraries = loadMaven();

        for (String library : libraries) {
            classpathBuilder.getContext().getLogger().info(
                Component.text("Loading library: " + library)
            );
            resolver.addDependency(new Dependency(new DefaultArtifact(library), null));
        }

        resolver.addRepository(
            new RemoteRepository.Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build()
        );

        classpathBuilder.addLibrary(resolver);
    }

    private List<String> loadMaven() {
        try (InputStream file = getClass().getResourceAsStream("/maven.libraries")) {
            assert file != null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8));

            var libraries = new LinkedList<String>();

            while(reader.ready()) {
                String line = reader.readLine();
                libraries.add(line);
            }

            return libraries;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
