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

import java.util.function.Function;

interface CodeGeneratingBuildParameter {

    String getType();

    String getValue();

    Identifier getId();

    static CodeGeneratingBuildParameter from(BuildParameter<?> parameter) {
        ParameterType type;
        if (parameter instanceof IntegerBuildParameter) {
            type = new ParameterType("int", "Integer", ".map(Integer::parseInt)", Function.identity());
        } else if (parameter instanceof BooleanBuildParameter) {
            type = new ParameterType("boolean", "Boolean", ".map(Boolean::parseBoolean)", Function.identity());
        } else if (parameter instanceof EnumBuildParameter) {
            String typeName = parameter.id.toFullQualifiedTypeName();
            type = new ParameterType(typeName, typeName, ".map(" + typeName + "::valueOf)", s -> typeName + "." + s);
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
            StringBuilder sb = new StringBuilder("providers.gradleProperty(\"")
              .append(parameter.id.toPropertyPath())
              .append("\")");

            //Setup property alias fallback
            for (String alias : parameter.getAliases().get()) {
              sb.append(".orElse(providers.gradleProperty(\"")
                .append(alias)
                .append("\"))");
            }

            //Setup ENV variable fallback
            if (parameter.getEnvironmentVariableName().isPresent()) {
                String envName = parameter.getEnvironmentVariableName().get();
                envName = envName.isEmpty() ? parameter.id.toEnvironmentVariableName() : envName;
                sb.append(".orElse(providers.environmentVariable(\"")
                  .append(envName)
                  .append("\"))");
            }

            sb.append(type.transformation)
              .append(".getOrElse(")
              .append(getDefaultValue())
              .append(")");

            return sb.toString();
        }

        private String getDefaultValue() {
            return type.defaultValueTransformation.apply(parameter.getDefaultValue().get().toString());
        }

        @Override
        public Identifier getId() {
            return parameter.id;
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
            StringBuilder sb = new StringBuilder("providers.gradleProperty(\"")
              .append(parameter.id.toPropertyPath())
              .append("\")");

            //Setup property alias fallback
            for (String alias : parameter.getAliases().get()) {
              sb.append(".orElse(providers.gradleProperty(\"")
                .append(alias)
                .append("\"))");
            }

            //Setup ENV variable fallback
            if (parameter.getEnvironmentVariableName().isPresent()) {
                String envName = parameter.getEnvironmentVariableName().get();
                envName = envName.isEmpty() ? parameter.id.toEnvironmentVariableName() : envName;
                sb.append(".orElse(providers.environmentVariable(\"")
                  .append(envName)
                  .append("\"))");
            }

            sb.append(type.transformation);

            return sb.toString();
        }

        @Override
        public Identifier getId() {
            return parameter.id;
        }
    }

    class ParameterType {

        final String name;
        final String typeParameter;
        final String transformation;
        final Function<String, String> defaultValueTransformation;

        ParameterType(String name, String typeParameter, String transformation, Function<String, String> defaultValueTransformation) {
            this.name = name;
            this.typeParameter = typeParameter;
            this.transformation = transformation;
            this.defaultValueTransformation = defaultValueTransformation;
        }
    }
}
