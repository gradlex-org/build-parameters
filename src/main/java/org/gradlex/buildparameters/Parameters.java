package org.gradlex.buildparameters;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Console;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.logging.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public abstract class Parameters extends DefaultTask {

    private final static Logger LOGGER = (Logger) LoggerFactory.getLogger(Parameters.class);

    @Console
    public abstract Property<BuildParameterGroup> getRootBuildParameterGroup();

    @TaskAction
    public void printParameters() {

        print("------------------------------------------------------------");
        print("Supported Build Parameters");
        print("------------------------------------------------------------");

        printGroup(getRootBuildParameterGroup().get());

        print("");
        print("To set a parameter use -Pparameter.name=value");
        print("");
        print("To see more detail about a parameter, run gradlew parameters --details <parameter.name>");
    }

    private void printGroup(BuildParameterGroup buildParameterGroup) {
        String header = printPropertyPath(buildParameterGroup.getPropertyPath(), buildParameterGroup.getDescription());
        if (!header.isEmpty()) {
            print(String.join("", Collections.nCopies(header.length(), "-")));
        }

        for (BuildParameter<?> parameter : buildParameterGroup.getParameters().get()) {
            String enumValues = "";
            if (parameter instanceof EnumBuildParameter) {
                enumValues = " [";
                enumValues += String.join(", ", ((EnumBuildParameter) parameter).getValues().get());
                enumValues += "]";
            }

            printPropertyPath(parameter.getPropertyPath(), parameter.getDescription());
            // print("  Type: " + parameter.getClass().getSimpleName().replace("BuildParameter_Decorated", "") + enumValues);
            // print("  Default value: " + parameter.getDefaultValue().map(Object::toString).getOrElse("(none)"));
        }

        for (BuildParameterGroup subGroup : buildParameterGroup.getGroups().get()) {
            printGroup(subGroup);
        }
    }

    private String printPropertyPath(String propertyPath, Property<String> description) {
        String line = propertyPath + description.map(d -> " - " + d).getOrElse("");
        print(line);
        return line;
    }

    private void print(String line) {
        LOGGER.lifecycle(line);
    }

}
