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
