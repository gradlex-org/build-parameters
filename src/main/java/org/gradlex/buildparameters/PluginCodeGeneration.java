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

import static org.gradlex.buildparameters.Constants.GENERATED_EXTENSION_CLASS_NAME;
import static org.gradlex.buildparameters.Constants.GENERATED_EXTENSION_NAME;
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
        BuildParameterGroup baseGroup = getBaseGroup().get();
        getOutputDirectory().get().dir(baseGroup.id.toPackageFolderPath()).getAsFile().mkdirs();

        generateGroupClass(baseGroup);

        Path pluginSource = getOutputDirectory().get().file(baseGroup.id.toPackageFolderPath() + "/" + PLUGIN_CLASS_NAME + ".java").getAsFile().toPath();
        write(pluginSource, Arrays.asList(
                "package " + baseGroup.id.toPackageName() + ";",
                "",
                "import org.gradle.api.Plugin;",
                "import org.gradle.api.plugins.ExtensionAware;",
                "",
                "public abstract class " + PLUGIN_CLASS_NAME + " implements Plugin<ExtensionAware> {",
                "    @Override",
                "    public void apply(ExtensionAware projectOrSettings) {",
                "        projectOrSettings.getExtensions().create(\"" + GENERATED_EXTENSION_NAME + "\", " + GENERATED_EXTENSION_CLASS_NAME + ".class);",
                "    }",
                "}"
        ));
    }

    private void generateGroupClass(BuildParameterGroup group) {
        List<BuildParameterGroup> subGroups = group.getGroups().get();
        List<BuildParameter<?>> buildParameters = group.getParameters().get();

        subGroups.forEach(this::generateGroupClass);
        buildParameters.stream().filter(p -> p instanceof EnumBuildParameter).forEach(p -> generateEnumClass((EnumBuildParameter) p));

        List<CodeGeneratingBuildParameter> parameters = buildParameters.stream().map(p -> CodeGeneratingBuildParameter.from(p, group)).collect(toList());
        String groupClassName = group.id.toSimpleTypeName();

        getOutputDirectory().get().dir(group.id.toPackageFolderPath()).getAsFile().mkdirs();
        Path groupSource = getOutputDirectory().get().file(group.id.toPackageFolderPath() + "/" + groupClassName + ".java").getAsFile().toPath();

        List<String> lines = new ArrayList<>();
        lines.add("package " + group.id.toPackageName() + ";");
        lines.add("");
        lines.add("import org.gradle.api.model.ObjectFactory;");
        lines.add("import org.gradle.api.provider.ProviderFactory;");
        lines.add("import javax.inject.Inject;");
        lines.add("");
        lines.add("public abstract class " + groupClassName + " {");
        for (BuildParameterGroup subGroup : subGroups) {
            lines.add("    private final " + subGroup.id.toFullQualifiedTypeName() + " " + subGroup.id.toFieldName() + ";");
        }
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("    private final " + parameter.getType() + " " + parameter.getId().toFieldName() + ";");
        }
        lines.add("    @Inject");
        lines.add("    public " + groupClassName + "(ProviderFactory providers, ObjectFactory objects) {");
        for (BuildParameterGroup subGroup : subGroups) {
            lines.add("        this." + subGroup.id.toFieldName() + " = objects.newInstance(" + subGroup.id.toFullQualifiedTypeName() + ".class, providers, objects);");
        }
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("        this." + parameter.getId().toFieldName() + " = " + parameter.getValue() + ";");
        }
        lines.add("    }");
        for (BuildParameterGroup subGroup : subGroups) {
            lines.add("    public " + subGroup.id.toFullQualifiedTypeName() + " get" + subGroup.id.toSimpleTypeName() + "() {");
            lines.add("        return this." + subGroup.id.toFieldName() + ";");
            lines.add("    }");
        }
        for (CodeGeneratingBuildParameter parameter : parameters) {
            lines.add("    public " + parameter.getType() + " get" + capitalize(parameter.getId().toFieldName()) + "() {");
            lines.add("        return this." + parameter.getId().toFieldName() + ";");
            lines.add("    }");
        }
        buildParameters.stream().filter(p -> p instanceof BooleanBuildParameter).forEach(p -> generateParseMethod((BooleanBuildParameter) p, lines));
        lines.add("}");

        write(groupSource, lines);
    }

    private void generateEnumClass(EnumBuildParameter enumBuildParameter) {
        getOutputDirectory().get().dir(enumBuildParameter.id.toPackageFolderPath()).getAsFile().mkdirs();
        Path enumSource = getOutputDirectory().get().file(enumBuildParameter.id.toPackageFolderPath() + "/" + enumBuildParameter.id.toSimpleTypeName() + ".java").getAsFile().toPath();

        List<String> lines = new ArrayList<>();
        lines.add("package " + enumBuildParameter.id.toPackageName() + ";");
        lines.add("");
        lines.add("public enum " + enumBuildParameter.id.toSimpleTypeName() + " {");
        for (String enumValue : enumBuildParameter.getValues().get()) {
            lines.add("    " + enumValue + ",");
        }
        lines.add("}");

        write(enumSource, lines);
    }

    private void generateParseMethod(BooleanBuildParameter p, List<String> lines) {
        lines.add("    private static boolean parse" + p.id.toSimpleTypeName() + "(String p) {");
        lines.add("        if (p.isEmpty() || p.equalsIgnoreCase(\"true\")) return true;");
        lines.add("        if (p.equalsIgnoreCase(\"false\")) return false;");
        lines.add("        throw new RuntimeException(\"Value '\" + p + \"' for parameter '" + p.id.toPropertyPath() + "' is not a valid boolean value - use 'true' (or '') / 'false'\");");
        lines.add("    }");
    }

    private static void write(Path file, List<String> lines) {
        try {
            Files.write(file, lines);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
