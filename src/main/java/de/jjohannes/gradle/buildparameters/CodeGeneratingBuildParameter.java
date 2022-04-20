package de.jjohannes.gradle.buildparameters;

interface CodeGeneratingBuildParameter {

    String getType();

    String getValue();

    String getName();

    static CodeGeneratingBuildParameter from(BuildParameter<?> parameter) {
        ParameterType type;
        if (parameter instanceof IntegerBuildParameter) {
            type = new ParameterType("int", "Integer", ".map(Integer::parseInt)", false);
        } else if (parameter instanceof BooleanBuildParameter) {
            type = new ParameterType("boolean", "Boolean", ".map(Boolean::parseBoolean)", false);
        } else {
            type = new ParameterType("String", "String", "", true);
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
            return type.requiresQuoting ? "\"" + parameter.getDefaultValue().get() + "\"" : parameter.getDefaultValue().get().toString();
        }

        @Override
        public String getName() {
            return parameter.getName();
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
        public String getName() {
            return parameter.getName();
        }
    }

    class ParameterType {

        final String name;
        final String typeParameter;
        final String transformation;
        final boolean requiresQuoting;

        ParameterType(String name, String typeParameter, String transformation, boolean requiresQuoting) {
            this.name = name;
            this.typeParameter = typeParameter;
            this.transformation = transformation;
            this.requiresQuoting = requiresQuoting;
        }
    }
}
