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
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.PluginDeclaration;

import javax.inject.Inject;

import static org.gradlex.buildparameters.Constants.PACKAGE_NAME;
import static org.gradlex.buildparameters.Constants.PLUGIN_CLASS_NAME;

public abstract class BuildParametersExtension extends BuildParameterGroup {

    private final PluginDeclaration pluginDeclaration;

    @Inject
    public BuildParametersExtension(GradlePluginDevelopmentExtension gradlePlugins) {
        super(new RootIdentifier());
        this.pluginDeclaration = gradlePlugins.getPlugins().create("build-parameters", p -> {
            p.setId("build-parameters");
            p.setImplementationClass(PACKAGE_NAME + "." + PLUGIN_CLASS_NAME);
        });
        getEnableValidation().convention(true);
    }

    /**
     * Change the plugin ID of the generated plugin (the default is 'build-parameters').
     *
     * @param pluginId The plugin ID for the generated plugin.
     */
    public void pluginId(String pluginId) {
        pluginDeclaration.setId(pluginId);
    }

    /**
     * By default, the generated plugin contains validation code that makes the build fail if the user
     * defines a -P parameter that is not a defined Build Parameter. E.g. this fails:
     * <code>
     *    ./gradlew help -Pthis.is.not.a.build.parameter=some_value
     * </code>
     * It also fails, if a build parameter is accidentally defined via '-D' instead of '-P'.
     * E.g. this fails:
     * <code>
     *    ./gradlew help -Dthis.is.a.build.parameter=some_value
     * </code>
     * Generating this validation code can be deactivated by setting this flag to 'true'.
     *
     * @return disable validation property
     */
    @Input
    public abstract Property<Boolean> getEnableValidation();
}
