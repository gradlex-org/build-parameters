// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import java.io.File;
import org.gradle.exemplar.model.Command;
import org.gradle.exemplar.model.Sample;
import org.gradle.exemplar.test.runner.SampleModifier;

public class SamplesIntegrationTestModifier implements SampleModifier {
    @Override
    public Sample modify(Sample sampleIn) {
        Command cmd = sampleIn.getCommands().remove(0);
        sampleIn.getCommands().add(copyCommand(cmd, "groovy"));
        sampleIn.getCommands().add(copyCommand(cmd, "kotlin"));
        return sampleIn;
    }

    private Command copyCommand(Command cmd, String executionSubdirectory) {
        File pluginProjectDir = new File(".");
        return new Command(
                new File(pluginProjectDir, "gradlew").getAbsolutePath(),
                executionSubdirectory,
                cmd.getArgs(),
                cmd.getFlags(),
                cmd.getExpectedOutput(),
                cmd.isExpectFailure(),
                cmd.isAllowAdditionalOutput(),
                cmd.isAllowDisorderedOutput(),
                cmd.getUserInputs());
    }
}
