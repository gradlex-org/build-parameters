package de.jjohannes.gradle.buildparameters;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public abstract class PluginCodeGeneration extends DefaultTask {

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void generate() throws IOException {
        // TODO java package structure
        Path extensionSource = getOutputDirectory().get().file("BuildParametersExtension.java").getAsFile().toPath();
        Files.write(extensionSource, Arrays.asList(
                "public abstract class BuildParametersExtension {",
                "    public String getDbHost() {",
                "        return \"localhost\";",
                "    }",
                "}"
        ));
        Path pluginSource = getOutputDirectory().get().file("BuildParametersPlugin.java").getAsFile().toPath();
        Files.write(pluginSource, Arrays.asList(
                "import org.gradle.api.Project;",
                "import org.gradle.api.Plugin;",
                "public class BuildParametersPlugin implements Plugin<Project> {",
                "    @Override",
                "    public void apply(Project project) {",
                "        project.getExtensions().create(\"buildParameters\", BuildParametersExtension.class);",
                "    }",
                "}"
        ));
    }

}
