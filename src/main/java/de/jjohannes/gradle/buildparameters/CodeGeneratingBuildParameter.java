package de.jjohannes.gradle.buildparameters;

interface CodeGeneratingBuildParameter {

    String getType();

    String getValue();

    String getName();

    static CodeGeneratingBuildParameter from(BuildParameter parameter) {
        if (parameter.getDefaultValue().isPresent()) {
            return new CodeGeneratingBuildParameter() {
                @Override
                public String getType() {
                    return "String";
                }

                @Override
                public String getValue() {
                    return "providers.gradleProperty(\"" + parameter.getPath() + "\").getOrElse(\"" + parameter.getDefaultValue().get() + "\")";
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
