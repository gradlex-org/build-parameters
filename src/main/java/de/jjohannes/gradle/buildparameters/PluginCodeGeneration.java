package de.jjohannes.gradle.buildparameters;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PluginCodeGeneration extends DefaultTask {

    @Nested
    public abstract ListProperty<BuildParameter> getParameters();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void generate() throws IOException {
        // TODO java package structure
        Path extensionSource = getOutputDirectory().get().file("BuildParametersExtension.java").getAsFile().toPath();
        final List<String> lines = new ArrayList<>();
        lines.add("public abstract class BuildParametersExtension {");
        for (BuildParameter parameter : getParameters().get()) {
            lines.add("    public String get" + capitalize(parameter.getName()) + "() {");
            lines.add("        return \"" + parameter.getDefaultValue().get() + "\";");
            lines.add("    }");
        }
        lines.add("}");
        Files.write(extensionSource, lines);

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

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
