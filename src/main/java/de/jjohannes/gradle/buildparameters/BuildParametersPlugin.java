package de.jjohannes.gradle.buildparameters;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin;

public class BuildParametersPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JavaGradlePluginPlugin.class);

        BuildParametersExtension buildParameters =
                project.getExtensions().create("buildParameters", BuildParametersExtension.class);
        // project.getExtensions().add("buildParameters",
        //        project.container(BuildParameter.class, name -> {  }));

        GradlePluginDevelopmentExtension gradlePlugins =
                project.getExtensions().getByType(GradlePluginDevelopmentExtension.class);

        // TODO generate some code

        gradlePlugins.getPlugins().create("my-build-params", p -> {
            p.setId("my-build-params-generated");
            p.setImplementationClass("my.some.GeneratedPlugin");
            p.setDescription("This thing is generated and gives you your parameters");
        });
    }
}
