// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import static org.gradle.internal.logging.text.StyledTextOutput.Style.Header;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Identifier;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Info;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Normal;

import java.util.Collections;
import java.util.Optional;
import javax.inject.Inject;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Console;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.internal.logging.text.StyledTextOutputFactory;
import org.gradle.internal.os.OperatingSystem;

/**
 * @since 1.4
 */
public abstract class Parameters extends DefaultTask {

    @Console
    @Option(option = "details", description = "Name of parameter to display detail information for")
    public abstract Property<String> getDetails();

    @Console
    public abstract Property<BuildParameterGroup> getRootBuildParameterGroup();

    @Console
    public abstract Property<String> getBuildPath();

    @Inject
    protected abstract StyledTextOutputFactory getTextOutputFactory();

    @TaskAction
    public void print() {
        StyledTextOutput output = getTextOutputFactory().create(getClass());
        if (getDetails().isPresent()) {
            printDetails(output);
        } else {
            printParameters(output);
        }
    }

    private void printDetails(StyledTextOutput output) {
        String propertyPath = getDetails().get();
        Optional<BuildParameter<?>> match = getRootBuildParameterGroup().get().findParameter(propertyPath);
        if (match.isPresent()) {
            BuildParameter<?> parameter = match.get();

            String type;
            String exampleValue;

            if (parameter instanceof StringBuildParameter) {
                type = "String";
                exampleValue = "\"a string value\"";
            } else if (parameter instanceof IntegerBuildParameter) {
                type = "Integer";
                exampleValue = "42";
            } else if (parameter instanceof BooleanBuildParameter) {
                type = "Boolean";
                exampleValue = ((BooleanBuildParameter) parameter)
                        .getDefaultValue()
                        .map(v -> v ? "false" : "true")
                        .getOrElse("false");
            } else if (parameter instanceof EnumBuildParameter) {
                type = "Enum";
                exampleValue =
                        ((EnumBuildParameter) parameter).getValues().get().get(0);
            } else {
                throw new IllegalStateException();
            }

            output.text("Detailed build parameter information for ");
            output.withStyle(Header).println(propertyPath);
            output.println();

            output.println("Type");
            output.withStyle(Header).println("     " + type);
            output.println();

            if (parameter.getDescription().isPresent()) {
                output.println("Description");
                output.withStyle(Header)
                        .println("     " + parameter.getDescription().get());
                output.println();
            }

            if (parameter instanceof EnumBuildParameter) {
                String enumValues = String.join(
                        ", ", ((EnumBuildParameter) parameter).getValues().get());
                output.println("Values");
                output.withStyle(Header).println("     " + enumValues);
                output.println();
            }

            output.println("Default value");
            output.withStyle(Header)
                    .println("     "
                            + parameter.getDefaultValue().map(Object::toString).getOrElse("(none)"));
            output.println();

            if (parameter.getEnvironmentVariableName().isPresent()) {
                String envName = parameter.getEnvironmentVariableName().get();
                output.println("Environment Variable");
                output.withStyle(Header).println("     " + envName);
                output.println();
            }

            output.println("Examples");
            if (parameter instanceof BooleanBuildParameter) {
                output.withStyle(Header).println("     -P" + propertyPath);
            }
            output.withStyle(Header).println("     -P" + propertyPath + "=" + exampleValue);
        } else {
            throw new RuntimeException("Unknown build parameter: " + propertyPath);
        }
    }

    private void printParameters(StyledTextOutput output) {
        output.style(Header);
        output.println("------------------------------------------------------------");
        output.println("Supported Build Parameters");
        output.println("------------------------------------------------------------");
        output.println();
        output.style(Normal);

        printGroup(getRootBuildParameterGroup().get(), output);

        output.text("To set a parameter use ");
        output.withStyle(Header).println("-Pparameter.name=value");
        output.println();

        String path = getBuildPath().get() + getPath();
        String gradlew = OperatingSystem.current() == OperatingSystem.WINDOWS ? "gradlew.bat" : "./gradlew";
        String cmd = gradlew + " " + path + " --details <parameter.name>";
        output.text("To see more detail about a parameter, run ");
        output.withStyle(Header).println(cmd);
    }

    private void printGroup(BuildParameterGroup buildParameterGroup, StyledTextOutput output) {
        if (!buildParameterGroup.getParameters().get().isEmpty()
                || buildParameterGroup.getDescription().isPresent()) {
            String header = buildParameterGroup.getPropertyPath()
                    + buildParameterGroup.getDescription().map(d1 -> " - " + d1).getOrElse("");
            if (!header.isEmpty()) {
                output.withStyle(Header).println(header);
                output.withStyle(Header).println(String.join("", Collections.nCopies(header.length(), "-")));
            }

            for (BuildParameter<?> parameter :
                    buildParameterGroup.getParameters().get()) {
                String propertyPath = parameter.getPropertyPath();
                Property<String> description = parameter.getDescription();
                output.withStyle(Identifier).text(propertyPath);
                output.withStyle(Info).println(description.map(d -> " - " + d).getOrElse(""));
            }
            output.println();
        }

        for (BuildParameterGroup subGroup : buildParameterGroup.getGroups().get()) {
            printGroup(subGroup, output);
        }
    }
}
