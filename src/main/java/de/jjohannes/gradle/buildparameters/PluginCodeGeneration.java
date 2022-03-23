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

import static de.jjohannes.gradle.buildparameters.Constants.PACKAGE_NAME;
import static de.jjohannes.gradle.buildparameters.Constants.PLUGIN_CLASS_NAME;
import static java.util.stream.Collectors.toList;

public abstract class PluginCodeGeneration extends DefaultTask {

    @Nested
    public abstract ListProperty<BuildParameter> getParameters();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void generate() throws IOException {
        List<CodeGeneratingBuildParameter> parameters = getParameters().get().stream().map(CodeGeneratingBuildParameter::from).collect(toList());
        String sourcesFolder = PACKAGE_NAME.replace(".", "/");
        getOutputDirectory().get().dir(sourcesFolder).getAsFile().mkdirs();

        Path extensionSource = getOutputDirectory().get().file(sourcesFolder + "/BuildParametersExtension.java").getAsFile().toPath();
        List<String> lines = new ArrayList<>();
        lines.add("package " + PACKAGE_NAME + ";");
        lines.add("");
        lines.add("import org.gradle.api.provider.ProviderFactory;");
        lines.add("import javax.inject.Inject;");
        lines.add("");
        lines.add("public abstract class BuildParametersExtension {");
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("    private final " + parameter.getType() + " " + parameter.getName() + ";");
        }
        lines.add("    @Inject");
        lines.add("    public BuildParametersExtension(ProviderFactory providers) {");
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("        this." + parameter.getName() + " = " + parameter.getValue() + ";");
        }
        lines.add("    }");
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("    public " + parameter.getType() + " get" + capitalize(parameter.getName()) + "() {");
            lines.add("        return this." + parameter.getName() + ";");
            lines.add("    }");
        }
        lines.add("}");
        Files.write(extensionSource, lines);

        Path pluginSource = getOutputDirectory().get().file(sourcesFolder + "/" + PLUGIN_CLASS_NAME + ".java").getAsFile().toPath();
        Files.write(pluginSource, Arrays.asList(
                "package " + PACKAGE_NAME + ";",
                "",
                "import org.gradle.api.Project;",
                "import org.gradle.api.Plugin;",
                "",
                "public class " + PLUGIN_CLASS_NAME + " implements Plugin<Project> {",
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
