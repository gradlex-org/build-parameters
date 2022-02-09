package de.jjohannes.gradle.buildparameters;

import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
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

        TaskProvider<PluginCodeGeneration> task = project.getTasks().register("generatePluginCode", PluginCodeGeneration.class, it -> {
            it.getOutputDirectory().convention(project.getLayout().getBuildDirectory().dir("generated-sources/build-parameters-plugin"));
        });
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceSet main = sourceSets.getByName("main");
        main.getJava().srcDir(task.flatMap(PluginCodeGeneration::getOutputDirectory));

        gradlePlugins.getPlugins().create("my-build-params", p -> {
            p.setId("my-build-params");
            p.setImplementationClass("BuildParametersPlugin");
            p.setDescription("This thing is generated and gives you your parameters");
        });
    }
}
