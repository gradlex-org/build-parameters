package de.jjohannes.gradle.buildparameters;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.UncheckedIOException;
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
    public abstract Property<BuildParameterGroup> getBaseGroup();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void generate() {
        getOutputDirectory().get().dir(getSourcesPath()).getAsFile().mkdirs();

        generateGroupClass(getBaseGroup().get());

        Path pluginSource = getOutputDirectory().get().file(getSourcesPath() + "/" + PLUGIN_CLASS_NAME + ".java").getAsFile().toPath();
        write(pluginSource, Arrays.asList(
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

    private void generateGroupClass(BuildParameterGroup group) {
        final List<BuildParameterGroup> subGroups = group.getGroups().get();
        subGroups.forEach(this::generateGroupClass);

        List<CodeGeneratingBuildParameter> parameters = group.getParameters().get().stream().map(CodeGeneratingBuildParameter::from).collect(toList());
        String groupClassName = capitalize(group.getName());
        Path groupSource = getOutputDirectory().get().file(getSourcesPath() + "/" + groupClassName + ".java").getAsFile().toPath();
        List<String> lines = new ArrayList<>();
        lines.add("package " + PACKAGE_NAME + ";");
        lines.add("");
        lines.add("import org.gradle.api.model.ObjectFactory;");
        lines.add("import org.gradle.api.provider.ProviderFactory;");
        lines.add("import javax.inject.Inject;");
        lines.add("");
        lines.add("public abstract class " + groupClassName + " {");
        for (BuildParameterGroup subGroup : subGroups) {
            lines.add("    private final " + capitalize(subGroup.getName()) + " " + subGroup.getName() + ";");
        }
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("    private final " + parameter.getType() + " " + parameter.getName() + ";");
        }
        lines.add("    @Inject");
        lines.add("    public " + groupClassName + "(ProviderFactory providers, ObjectFactory objects) {");
        for (BuildParameterGroup subGroup : subGroups) {
            lines.add("        this." + subGroup.getName() + " = objects.newInstance(" + capitalize(subGroup.getName()) + ".class);");
        }
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("        this." + parameter.getName() + " = " + parameter.getValue() + ";");
        }
        lines.add("    }");
        for (BuildParameterGroup subGroup : subGroups) {
            lines.add("    public " + capitalize(subGroup.getName()) + " get" + capitalize(subGroup.getName()) + "() {");
            lines.add("        return this." + subGroup.getName() + ";");
            lines.add("    }");
        }
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("    public " + parameter.getType() + " get" + capitalize(parameter.getName()) + "() {");
            lines.add("        return this." + parameter.getName() + ";");
            lines.add("    }");
        }
        lines.add("}");

        write(groupSource, lines);
    }

    private static String getSourcesPath() {
        return PACKAGE_NAME.replace(".", "/");
    }

    private static void write(Path file, List<String> lines) {
        try {
            Files.write(file, lines);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
