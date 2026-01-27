// SPDX-License-Identifier: Apache-2.0
package org.gradlex.buildparameters;

import static org.gradlex.buildparameters.Constants.GENERATED_EXTENSION_NAME;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.plugins.HelpTasksPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin;
import org.gradle.util.GradleVersion;

public class BuildParametersPlugin implements Plugin<Project> {

    private static final GradleVersion MINIMUM_SUPPORTED_VERSION = GradleVersion.version("7.1");

    @Override
    public void apply(Project project) {
        if (GradleVersion.current().compareTo(MINIMUM_SUPPORTED_VERSION) < 0) {
            throw new IllegalStateException("Plugin requires at least Gradle 7.1");
        }

        project.getPlugins().apply(JavaGradlePluginPlugin.class);

        GradlePluginDevelopmentExtension gradlePlugins =
                project.getExtensions().getByType(GradlePluginDevelopmentExtension.class);

        String extensionName = project.getExtensions().findByName(GENERATED_EXTENSION_NAME) != null
                ? "buildParametersDefinition"
                : "buildParameters";

        BuildParametersExtension extension =
                project.getExtensions().create(extensionName, BuildParametersExtension.class, gradlePlugins);

        TaskProvider<PluginCodeGeneration> task = project.getTasks()
                .register("generatePluginCode", PluginCodeGeneration.class, t -> {
                    t.setDescription("Generates code for compile-safe access to build parameters.");
                    t.getBaseGroup().convention(extension);
                    t.getOutputDirectory()
                            .convention(project.getLayout()
                                    .getBuildDirectory()
                                    .dir("generated/sources/build-parameters-plugin/java/main"));
                });
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceSet main = sourceSets.getByName("main");
        main.getJava().srcDir(task.flatMap(PluginCodeGeneration::getOutputDirectory));

        project.getTasks().register("parameters", Parameters.class, t -> {
            t.setGroup(HelpTasksPlugin.HELP_GROUP);
            t.setDescription("Displays the supported build parameters.");
            t.getRootBuildParameterGroup().convention(extension);
            t.getBuildPath().convention(getBuildPath(project.getGradle(), ""));
        });
    }

    private static String getBuildPath(Gradle gradle, String acc) {
        if (gradle.getParent() == null) {
            return acc;
        } else {
            return getBuildPath(
                    gradle.getParent(), ":" + gradle.getRootProject().getName() + acc);
        }
    }
}
