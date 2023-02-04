package org.gradlex.buildparameters;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Console;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.logging.text.StyledTextOutput;
import org.gradle.internal.logging.text.StyledTextOutputFactory;
import org.gradle.internal.os.OperatingSystem;

import javax.inject.Inject;
import java.util.Collections;

import static org.gradle.internal.logging.text.StyledTextOutput.Style.Header;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Identifier;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Info;
import static org.gradle.internal.logging.text.StyledTextOutput.Style.Normal;

public abstract class Parameters extends DefaultTask {

    private final StyledTextOutput output;

    @Console
    public abstract Property<BuildParameterGroup> getRootBuildParameterGroup();

    @Console
    public abstract Property<String> getBuildPath();

    @Inject
    protected abstract StyledTextOutputFactory getTextOutputFactory();

    public Parameters() {
        output = getTextOutputFactory().create(getClass());
    }

    @TaskAction
    public void printParameters() {
        output.style(Header);
        output.println("------------------------------------------------------------");
        output.println("Supported Build Parameters");
        output.println("------------------------------------------------------------");
        output.style(Normal);

        printGroup(getRootBuildParameterGroup().get());

        output.text("To set a parameter use ");
        output.withStyle(Header).println("-Pparameter.name=value");
        output.println();

        String path = getBuildPath().get() + getPath();
        String gradlew = OperatingSystem.current() == OperatingSystem.WINDOWS ? "gradlew.bat" : "./gradlew";
        String cmd = gradlew + " " + path + " --details <parameter.name>";
        output.text("To see more detail about a parameter, run ");
        output.withStyle(Header).println(cmd);
    }

    private void printGroup(BuildParameterGroup buildParameterGroup) {
        if (!buildParameterGroup.getParameters().get().isEmpty() || buildParameterGroup.getDescription().isPresent()) {
            String header = buildParameterGroup.getPropertyPath() + buildParameterGroup.getDescription().map(d1 -> " - " + d1).getOrElse("");
            output.withStyle(Header).println(header);
            if (!header.isEmpty()) {
                output.withStyle(Header).println(String.join("", Collections.nCopies(header.length(), "-")));
            }

            for (BuildParameter<?> parameter : buildParameterGroup.getParameters().get()) {
                String enumValues = "";
                if (parameter instanceof EnumBuildParameter) {
                    enumValues = " [";
                    enumValues += String.join(", ", ((EnumBuildParameter) parameter).getValues().get());
                    enumValues += "]";
                }

                String propertyPath = parameter.getPropertyPath();
                Property<String> description = parameter.getDescription();
                output.withStyle(Identifier).text(propertyPath);
                output.withStyle(Info).println(description.map(d -> " - " + d).getOrElse(""));
                // print("  Type: " + parameter.getClass().getSimpleName().replace("BuildParameter_Decorated", "") + enumValues);
                // print("  Default value: " + parameter.getDefaultValue().map(Object::toString).getOrElse("(none)"));
            }
            output.println();
        }

        for (BuildParameterGroup subGroup : buildParameterGroup.getGroups().get()) {
            printGroup(subGroup);
        }
    }
}
