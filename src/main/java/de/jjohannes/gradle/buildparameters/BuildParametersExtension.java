package de.jjohannes.gradle.buildparameters;

import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.PluginDeclaration;

import javax.inject.Inject;

import static de.jjohannes.gradle.buildparameters.Constants.PACKAGE_NAME;
import static de.jjohannes.gradle.buildparameters.Constants.PLUGIN_CLASS_NAME;

public abstract class BuildParametersExtension extends BuildParameterGroup {

    private final PluginDeclaration pluginDeclaration;

    @Inject
    public BuildParametersExtension(GradlePluginDevelopmentExtension gradlePlugins) {
        super(Identifier.root());
        this.pluginDeclaration = gradlePlugins.getPlugins().create("build-parameters", p -> {
            p.setId("build-parameters");
            p.setImplementationClass(PACKAGE_NAME + "." + PLUGIN_CLASS_NAME);
        });
    }

    public void pluginId(String pluginId) {
        pluginDeclaration.setId(pluginId);
    }

    @Override
    public String getSimpleName() {
        return "buildParametersExtension";
    }

    @Override
    public String getName() {
        return "BuildParametersExtension";
    }
}
