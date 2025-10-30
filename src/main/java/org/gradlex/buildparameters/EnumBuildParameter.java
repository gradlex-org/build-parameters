// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import javax.inject.Inject;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;

public abstract class EnumBuildParameter extends BuildParameter<String> {

    @Input
    public abstract ListProperty<String> getValues();

    @Inject
    public EnumBuildParameter(Identifier identifier) {
        super(identifier);
    }
}
