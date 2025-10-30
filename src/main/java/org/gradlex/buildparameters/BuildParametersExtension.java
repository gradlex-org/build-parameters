// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import static org.gradlex.buildparameters.Constants.PACKAGE_NAME;
import static org.gradlex.buildparameters.Constants.PLUGIN_CLASS_NAME;

import javax.inject.Inject;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.PluginDeclaration;

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
     * @since 1.4
     */
    @Input
    public abstract Property<Boolean> getEnableValidation();
}
