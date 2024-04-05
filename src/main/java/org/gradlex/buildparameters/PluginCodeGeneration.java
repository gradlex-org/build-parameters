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
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Nested;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.gradlex.buildparameters.Constants.GENERATED_EXTENSION_CLASS_NAME;
import static org.gradlex.buildparameters.Constants.GENERATED_EXTENSION_NAME;
import static org.gradlex.buildparameters.Constants.JAVA_KEYWORDS;
import static org.gradlex.buildparameters.Constants.PLUGIN_CLASS_NAME;
import static org.gradlex.buildparameters.Constants.SPECIAL_IDENTIFIER_CHARACTERS;
import static org.gradlex.buildparameters.Strings.capitalize;

@CacheableTask
public abstract class PluginCodeGeneration extends DefaultTask {

    @Nested
    public abstract Property<BuildParametersExtension> getBaseGroup();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @TaskAction
    public void generate() {
        BuildParameterGroup baseGroup = getBaseGroup().get();
        getOutputDirectory().get().dir(baseGroup.id.toPackageFolderPath()).getAsFile().mkdirs();

        generateGroupClass(baseGroup);

        List<String> allParameters = new ArrayList<>();
        collectAllParameters(baseGroup, allParameters);

        List<String> validationCode = validationCode();

        Path pluginSource = getOutputDirectory().get().file(baseGroup.id.toPackageFolderPath() + "/" + PLUGIN_CLASS_NAME + ".java").getAsFile().toPath();
        write(pluginSource, Arrays.asList(
                "package " + baseGroup.id.toPackageName() + ";",
                "",
                "import org.gradle.api.Plugin;",
                "import org.gradle.api.Project;",
                "import org.gradle.api.initialization.Settings;",
                "import org.gradle.api.invocation.Gradle;",
                "import org.gradle.api.plugins.ExtensionAware;",
                "import java.util.Arrays;",
                "import java.util.List;",
                "import java.util.Set;",
                "import java.util.stream.Collectors;",
                "",
                "public abstract class " + PLUGIN_CLASS_NAME + " implements Plugin<ExtensionAware> {",
                "",
                "    private static final List<String> ALL_PARAMETERS = Arrays.asList(",
                        allParameters.stream().map(p -> "        \"" + p + "\"").collect(Collectors.joining(",\n")),
                "    );",
                "",
                "    @Override",
                "    public void apply(ExtensionAware projectOrSettings) {",
                        String.join("\n", validationCode),
                "        projectOrSettings.getExtensions().create(\"" + GENERATED_EXTENSION_NAME + "\", " + GENERATED_EXTENSION_CLASS_NAME + ".class);",
                "    }",
                "}"
        ));
    }

    private List<String> validationCode() {
        if (!getBaseGroup().get().getEnableValidation().get()) {
            return Collections.emptyList();
        }
        return Arrays.asList(
                "        Gradle gradle = projectOrSettings instanceof Project ? ((Project) projectOrSettings).getGradle() : ((Settings) projectOrSettings).getGradle();",
                "        boolean validationActive = !gradle.getStartParameter().getSystemPropertiesArgs().keySet().contains(\"idea.version\");",
                "        Set<String> startParameters = gradle.getStartParameter().getProjectProperties().keySet().stream()",
                "            .filter(p -> !p.startsWith(\"org.gradle\"))",
                "            .collect(Collectors.toSet());",
                "        for (String parameter : startParameters) {",
                "            if (validationActive && !ALL_PARAMETERS.contains(parameter)) {",
                "                throw new RuntimeException(\"Unknown build parameter: \" + parameter);",
                "            }",
                "        }",
                "        for (String parameter : gradle.getStartParameter().getSystemPropertiesArgs().keySet()) {",
                "            if (validationActive && ALL_PARAMETERS.contains(parameter)) {",
                "                throw new RuntimeException(\"Build parameter defined via '-D\" + parameter + \"'! Use '-P\" + parameter + \"' instead\");",
                "            }",
                "        }"
        );
    }

    private void collectAllParameters(BuildParameterGroup group, List<String> allParameters) {
        for (BuildParameter<?> parameter : group.getParameters().get()) {
            allParameters.add(parameter.getPropertyPath());
        }
        for (BuildParameterGroup subGroup: group.getGroups().get()) {
            collectAllParameters(subGroup, allParameters);
        }
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
            renderJavaDoc(lines, subGroup.getDescription());
            lines.add("    public " + subGroup.id.toFullQualifiedTypeName() + " get" + subGroup.id.toSimpleTypeName() + "() {");
            lines.add("        return this." + subGroup.id.toFieldName() + ";");
            lines.add("    }");
        }
        for (CodeGeneratingBuildParameter parameter : parameters) {
            renderJavaDoc(lines, parameter.getDescription());
            lines.add("    public " + parameter.getType() + " get" + capitalize(parameter.getId().toFieldName()) + "() {");
            lines.add("        return this." + parameter.getId().toFieldName() + ";");
            lines.add("    }");
        }
        buildParameters.stream().filter(p -> p instanceof BooleanBuildParameter).forEach(p -> generateBooleanParseMethod((BooleanBuildParameter) p, lines));
        lines.add("}");

        write(groupSource, lines);
    }

    private void renderJavaDoc(List<String> lines, Property<String> description) {
        if (description.isPresent()) {
            List<String> descriptionLines = Arrays.stream(description.get().split(System.lineSeparator()))
                    .map(String::trim)
                    .collect(Collectors.toCollection(ArrayList::new));

            StringLists.dropLeadingAndTrailingEmptyLines(descriptionLines);

            lines.add("    /**");
            descriptionLines.forEach(l -> lines.add("     * " + l.trim()));
            lines.add("     */");
        }
    }

    private void generateEnumClass(EnumBuildParameter enumBuildParameter) {
        getOutputDirectory().get().dir(enumBuildParameter.id.toPackageFolderPath()).getAsFile().mkdirs();
        String typeName = enumBuildParameter.id.toSimpleTypeName();
        List<String> values = enumBuildParameter.getValues().get();

        Path enumSource = getOutputDirectory().get().file(enumBuildParameter.id.toPackageFolderPath() + "/" + typeName + ".java").getAsFile().toPath();

        List<String> lines = new ArrayList<>();
        lines.add("package " + enumBuildParameter.id.toPackageName() + ";");
        lines.add("");
        lines.add("public enum " + typeName + " {");
        for (String enumValue : values) {
            lines.add("    " + escapeEnumValue(enumValue) + ",");
        }
        lines.add("    ;");
        lines.add("");
        generateEnumParseMethod(typeName, values, lines);
        lines.add("}");

        write(enumSource, lines);
    }

    private void generateEnumParseMethod(String typeName, List<String> values, List<String> lines) {
        lines.add("    public static " + typeName + " parse(String value) {");
        for (String enumValue : values) {
            String escapedValue = escapeEnumValue(enumValue);
            if (!enumValue.equals(escapedValue)) {
                lines.add("        if (\"" + enumValue + "\".equals(value)) return " + typeName + "." + escapedValue + ";");
            }
        }
        lines.add("        return " + typeName + ".valueOf(value);");
        lines.add("    }");
        lines.add("");
        lines.add("    @Override");
        lines.add("    public String toString() {");
        for (String enumValue : values) {
            String escapedValue = escapeEnumValue(enumValue);
            if (!enumValue.equals(escapedValue)) {
                lines.add("        if (this == " + escapedValue + ") return \"" + enumValue + "\";");
            }
        }
        lines.add("        return name();");
        lines.add("    }");
    }

    private void generateBooleanParseMethod(BooleanBuildParameter p, List<String> lines) {
        lines.add("    private static boolean parse" + p.id.toSimpleTypeName() + "(String p) {");
        lines.add("        if (p.isEmpty() || p.equalsIgnoreCase(\"true\") || p.equals(\"1\")) return true;");
        lines.add("        if (p.equalsIgnoreCase(\"false\")) return false;");
        lines.add("        throw new RuntimeException(\"Value '\" + p + \"' for parameter '" + p.id.toPropertyPath() + "' is not a valid boolean value. Allowed values are strings 'true', '1', and empty string for true, and string 'false' for false\");");
        lines.add("    }");
    }

    static String escapeEnumValue(String value) {
        if (JAVA_KEYWORDS.contains(value)) {
            return "_" + value;
        }

        String escapedValue = value;
        for (char character : SPECIAL_IDENTIFIER_CHARACTERS) {
            escapedValue = escapedValue.replace(character, '_');
        }
        return escapedValue;
    }

    private static void write(Path file, List<String> lines) {
        try {
            Files.write(file, lines);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
