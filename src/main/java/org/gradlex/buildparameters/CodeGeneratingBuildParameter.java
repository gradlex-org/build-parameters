// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import static org.gradlex.buildparameters.PluginCodeGeneration.escapeEnumValue;

import java.util.function.Function;
import org.gradle.api.provider.Property;

interface CodeGeneratingBuildParameter {

    String getType();

    String getValue();

    Identifier getId();

    Property<String> getDescription();

    static CodeGeneratingBuildParameter from(BuildParameter<?> parameter, BuildParameterGroup containingGroup) {
        ParameterType type;
        if (parameter instanceof IntegerBuildParameter) {
            type = new ParameterType("int", "Integer", ".map(Integer::parseInt)", Function.identity());
        } else if (parameter instanceof BooleanBuildParameter) {
            type = new ParameterType(
                    "boolean",
                    "Boolean",
                    ".map(" + containingGroup.id.toSimpleTypeName() + "::parse" + parameter.id.toSimpleTypeName() + ")",
                    Function.identity());
        } else if (parameter instanceof EnumBuildParameter) {
            String typeName = parameter.id.toFullQualifiedTypeName();
            type = new ParameterType(
                    typeName, typeName, ".map(" + typeName + "::parse)", s -> typeName + "." + escapeEnumValue(s));
        } else {
            type = new ParameterType("String", "String", "", s -> "\"" + s + "\"");
        }

        if (parameter.getDefaultValue().isPresent()) {
            return new ParameterWithDefault(parameter, type);
        } else {
            return new ParameterWithoutDefault(parameter, type);
        }
    }

    class ParameterWithDefault implements CodeGeneratingBuildParameter {
        private final BuildParameter<?> parameter;
        private final ParameterType type;

        public ParameterWithDefault(BuildParameter<?> parameter, ParameterType type) {
            this.parameter = parameter;
            this.type = type;
        }

        @Override
        public String getType() {
            return type.name;
        }

        @Override
        public String getValue() {
            if (parameter.getEnvironmentVariableName().isPresent()) {
                String envName = parameter.getEnvironmentVariableName().get();
                return "providers.gradleProperty(\"" + parameter.id.toPropertyPath()
                        + "\").orElse(providers.environmentVariable(\"" + envName + "\"))" + type.transformation
                        + ".getOrElse(" + getDefaultValue() + ")";
            }
            return "providers.gradleProperty(\"" + parameter.id.toPropertyPath() + "\")" + type.transformation
                    + ".getOrElse(" + getDefaultValue() + ")";
        }

        private String getDefaultValue() {
            return type.defaultValueTransformation.apply(
                    parameter.getDefaultValue().get().toString());
        }

        @Override
        public Identifier getId() {
            return parameter.id;
        }

        @Override
        public Property<String> getDescription() {
            return parameter.getDescription();
        }
    }

    class ParameterWithoutDefault implements CodeGeneratingBuildParameter {
        private final BuildParameter<?> parameter;
        private final ParameterType type;

        public ParameterWithoutDefault(BuildParameter<?> parameter, ParameterType type) {
            this.parameter = parameter;
            this.type = type;
        }

        @Override
        public String getType() {
            return "org.gradle.api.provider.Provider<" + type.typeParameter + ">";
        }

        @Override
        public String getValue() {
            if (parameter.getEnvironmentVariableName().isPresent()) {
                String envName = parameter.getEnvironmentVariableName().get();
                return "providers.gradleProperty(\"" + parameter.id.toPropertyPath()
                        + "\").orElse(providers.environmentVariable(\"" + envName + "\"))" + errorIfMandatory()
                        + type.transformation;
            } else {
                return "providers.gradleProperty(\"" + parameter.id.toPropertyPath() + "\")" + errorIfMandatory()
                        + type.transformation;
            }
        }

        @Override
        public Identifier getId() {
            return parameter.id;
        }

        @Override
        public Property<String> getDescription() {
            return parameter.getDescription();
        }

        private String errorIfMandatory() {
            if (!parameter.getMandatory().get()) {
                return "";
            }

            String description = parameter.getDescription().isPresent()
                    ? " (" + parameter.getDescription().get() + ")"
                    : "";
            String envVariable = parameter.getEnvironmentVariableName().isPresent()
                    ? "  " + parameter.getEnvironmentVariableName().get() + "=value (environment variable)\\n"
                    : "";

            return ".orElse(providers.provider(() -> { throw new RuntimeException(\"" + "Build parameter "
                    + parameter.id.toPropertyPath() + description + " not set. Use one of the following:\\n" + "  -P"
                    + parameter.id.toPropertyPath() + "=value (command line)\\n" + "  "
                    + parameter.id.toPropertyPath() + "=value (in 'gradle.properties' file)\\n" + envVariable
                    + "\"); }))";
        }
    }

    class ParameterType {

        final String name;
        final String typeParameter;
        final String transformation;
        final Function<String, String> defaultValueTransformation;

        ParameterType(
                String name,
                String typeParameter,
                String transformation,
                Function<String, String> defaultValueTransformation) {
            this.name = name;
            this.typeParameter = typeParameter;
            this.transformation = transformation;
            this.defaultValueTransformation = defaultValueTransformation;
        }
    }
}
