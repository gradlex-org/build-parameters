package de.jjohannes.gradle.buildparameters;

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

        GradlePluginDevelopmentExtension gradlePlugins =
                project.getExtensions().getByType(GradlePluginDevelopmentExtension.class);
        BuildParametersExtension extension =
                project.getExtensions().create("buildParameters", BuildParametersExtension.class, project.getObjects(), gradlePlugins);

        TaskProvider<PluginCodeGeneration> task = project.getTasks().register("generatePluginCode", PluginCodeGeneration.class, it -> {
            it.getParameters().convention(extension.getParameters());
            it.getOutputDirectory().convention(project.getLayout().getBuildDirectory().dir("generated/sources/build-parameters-plugin/java/main"));
        });
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceSet main = sourceSets.getByName("main");
        main.getJava().srcDir(task.flatMap(PluginCodeGeneration::getOutputDirectory));
    }
}
