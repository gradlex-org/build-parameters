package de.jjohannes.gradle.buildparameters;

interface CodeGeneratingBuildParameter {

    String getType();

    String getValue();

    String getName();

    static CodeGeneratingBuildParameter from(BuildParameter<?> parameter) {
        boolean isInteger = parameter instanceof IntegerBuildParameter;
        boolean isBoolean = parameter instanceof BooleanBuildParameter;

        if (parameter.getDefaultValue().isPresent()) {
            return new CodeGeneratingBuildParameter() {
                @Override
                public String getType() {
                    return isInteger ? "int" : isBoolean ? "boolean" : "String";
                }

                @Override
                public String getValue() {
                    return isInteger
                            ? "providers.gradleProperty(\"" + parameter.getPath() + "\").map(Integer::parseInt).getOrElse(" + parameter.getDefaultValue().get() + ")" : isBoolean
                            ? "providers.gradleProperty(\"" + parameter.getPath() + "\").map(Boolean::parseBoolean).getOrElse(" + parameter.getDefaultValue().get() + ")"
                            : "providers.gradleProperty(\"" + parameter.getPath() + "\").getOrElse(\"" + parameter.getDefaultValue().get() + "\")";
                }

                @Override
                public String getName() {
                    return parameter.getName();
                }
            };
        } else {
            return new CodeGeneratingBuildParameter() {
                @Override
                public String getType() {
                    return "org.gradle.api.provider.Provider<String>";
                }

                @Override
                public String getValue() {
                    return "providers.gradleProperty(\"" + parameter.getPath() + "\")";
                }

                @Override
                public String getName() {
                    return parameter.getName();
                }
            };
        }
    }
}
