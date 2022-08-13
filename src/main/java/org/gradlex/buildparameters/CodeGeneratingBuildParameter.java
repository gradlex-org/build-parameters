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

import static org.gradlex.buildparameters.Strings.capitalize;

interface CodeGeneratingBuildParameter {

    String getType();

    String getValue();

    String getSimpleName();

    static CodeGeneratingBuildParameter from(BuildParameter<?> parameter) {
        ParameterType type;
        if (parameter instanceof IntegerBuildParameter) {
            type = new ParameterType("int", "Integer", ".map(Integer::parseInt)", Function.identity());
        } else if (parameter instanceof BooleanBuildParameter) {
            type = new ParameterType("boolean", "Boolean", ".map(Boolean::parseBoolean)", Function.identity());
        } else if (parameter instanceof EnumBuildParameter) {
            String typeName = capitalize(parameter.getSimpleName());
            type = new ParameterType(typeName, typeName, ".map(" + typeName + "::valueOf)", s -> Constants.PACKAGE_NAME + "." + typeName + "." + s);
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
            return "providers.gradleProperty(\"" + parameter.getPath() + "\")" + type.transformation + ".getOrElse(" + getDefaultValue() + ")";
        }

        private String getDefaultValue() {
            return type.defaultValueTransformation.apply(parameter.getDefaultValue().get().toString());
        }

        @Override
        public String getSimpleName() {
            return parameter.getSimpleName();
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
            return "providers.gradleProperty(\"" + parameter.getPath() + "\")" + type.transformation;
        }

        @Override
        public String getSimpleName() {
            return parameter.getSimpleName();
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
