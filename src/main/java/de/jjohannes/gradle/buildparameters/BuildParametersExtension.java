package de.jjohannes.gradle.buildparameters;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.PluginDeclaration;

import javax.inject.Inject;

import static de.jjohannes.gradle.buildparameters.Constants.PACKAGE_NAME;
import static de.jjohannes.gradle.buildparameters.Constants.PLUGIN_CLASS_NAME;

public abstract class BuildParametersExtension {

    private final ObjectFactory objects;
    private final PluginDeclaration pluginDeclaration;

    @Inject
    public BuildParametersExtension(Project project, GradlePluginDevelopmentExtension gradlePlugins) {
        this.objects = project.getObjects();
        this.pluginDeclaration = gradlePlugins.getPlugins().create("build-parameters", p -> {
            p.setId("build-parameters");
            p.setImplementationClass(PACKAGE_NAME + "." + PLUGIN_CLASS_NAME);
        });
    }

    public void parameter(String name, Action<? super BuildParameter> configure) {
        BuildParameter parameter = objects.newInstance(BuildParameter.class, name);
        configure.execute(parameter);
        getParameters().add(parameter);
    }

    public void pluginId(String pluginId) {
        pluginDeclaration.setId(pluginId);
    }

    public abstract ListProperty<BuildParameter> getParameters();
}
