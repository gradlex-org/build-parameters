package de.jjohannes.gradle.buildparameters;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.PluginDeclaration;

import javax.inject.Inject;

public abstract class BuildParametersExtension {

    private final ObjectFactory objects;
    private final PluginDeclaration pluginDeclaration;

    @Inject
    public BuildParametersExtension(ObjectFactory objects, GradlePluginDevelopmentExtension gradlePlugins) {
        this.objects = objects;
        this.pluginDeclaration = gradlePlugins.getPlugins().create("my-build-params", p -> {
            p.setId("my-build-params");
            p.setImplementationClass("BuildParametersPlugin");
            p.setDescription("This thing is generated and gives you your parameters");
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
