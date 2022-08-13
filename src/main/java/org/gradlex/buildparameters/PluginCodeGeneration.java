/*
 * Copyright 2022 the GradleX team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradlex.buildparameters;

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

import static org.gradlex.buildparameters.Constants.PACKAGE_NAME;
import static org.gradlex.buildparameters.Constants.PLUGIN_CLASS_NAME;
import static org.gradlex.buildparameters.Strings.capitalize;
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
        List<BuildParameterGroup> subGroups = group.getGroups().get();
        List<BuildParameter<?>> buildParameters = group.getParameters().get();

        subGroups.forEach(this::generateGroupClass);
        buildParameters.stream().filter(p -> p instanceof EnumBuildParameter).forEach(p -> generateEnumClass((EnumBuildParameter) p));

        List<CodeGeneratingBuildParameter> parameters = buildParameters.stream().map(CodeGeneratingBuildParameter::from).collect(toList());
        String groupClassName = group.getName();
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
            lines.add("    private final " + subGroup.getName() + " " + subGroup.getSimpleName() + ";");
        }
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("    private final " + parameter.getType() + " " + parameter.getSimpleName() + ";");
        }
        lines.add("    @Inject");
        lines.add("    public " + groupClassName + "(ProviderFactory providers, ObjectFactory objects) {");
        for (BuildParameterGroup subGroup : subGroups) {
            lines.add("        this." + subGroup.getSimpleName() + " = objects.newInstance(" + subGroup.getName() + ".class);");
        }
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("        this." + parameter.getSimpleName() + " = " + parameter.getValue() + ";");
        }
        lines.add("    }");
        for (BuildParameterGroup subGroup : subGroups) {
            lines.add("    public " + subGroup.getName() + " get" + capitalize(subGroup.getSimpleName()) + "() {");
            lines.add("        return this." + subGroup.getSimpleName() + ";");
            lines.add("    }");
        }
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("    public " + parameter.getType() + " get" + capitalize(parameter.getSimpleName()) + "() {");
            lines.add("        return this." + parameter.getSimpleName() + ";");
            lines.add("    }");
        }
        lines.add("}");

        write(groupSource, lines);
    }

    private void generateEnumClass(EnumBuildParameter enumBuildParameter) {
        String enumClassName = capitalize(enumBuildParameter.getSimpleName());
        Path enumSource = getOutputDirectory().get().file(getSourcesPath() + "/" + enumClassName + ".java").getAsFile().toPath();

        List<String> lines = new ArrayList<>();
        lines.add("package " + PACKAGE_NAME + ";");
        lines.add("");
        lines.add("public enum " + enumClassName + " {");
        for (String enumValue : enumBuildParameter.getValues().get()) {
            lines.add("    " + enumValue + ",");
        }
        lines.add("}");

        write(enumSource, lines);
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

}
