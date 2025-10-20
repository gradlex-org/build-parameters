// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

public abstract class BuildParameter<ParameterType> {

    final Identifier id;

    protected BuildParameter(Identifier identifier) {
        this.id = identifier;
    }

    @Input
    public String getPropertyPath() {
        return id.toPropertyPath();
    }

    @Input
    public abstract Property<Boolean> getMandatory();

    @Input
    @Optional
    public abstract Property<ParameterType> getDefaultValue();

    @Input
    @Optional
    public abstract Property<String> getDescription();

    @Input
    @Optional
    public abstract Property<String> getEnvironmentVariableName();

    public void fromEnvironment() {
        getEnvironmentVariableName().set(id.toEnvironmentVariableName());
    }

    public void fromEnvironment(String variableName) {
        getEnvironmentVariableName().set(variableName);
    }
}
