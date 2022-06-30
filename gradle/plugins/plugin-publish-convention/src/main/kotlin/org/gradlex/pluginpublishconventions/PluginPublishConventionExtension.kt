package org.gradlex.pluginpublishconventions

import com.gradle.publish.PluginBundleExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

abstract class PluginPublishConventionExtension(
    project: Project,
    gradlePlugin: GradlePluginDevelopmentExtension,
    private val pluginBundle: PluginBundleExtension
) {

    private val pluginDefinition = gradlePlugin.plugins.create(project.name)

    val id: Provider<String> = project.provider { pluginDefinition.id }
    val implementationClass: Provider<String> = project.provider { pluginDefinition.implementationClass }
    val displayName: Provider<String> = project.provider { pluginDefinition.displayName!! }
    val description: Provider<String> = project.provider { pluginDefinition.description!! }
    val tags: Provider<Collection<String>> = project.provider { pluginBundle.tags }
    val gitHub: Provider<String> =project.provider { pluginBundle.website }

    fun id(id: String) {
        pluginDefinition.id = id
    }

    fun implementationClass(implementationClass: String) {
        pluginDefinition.implementationClass = implementationClass
    }

    fun displayName(displayName: String) {
        pluginDefinition.displayName = displayName
    }

    fun description(description: String) {
        pluginDefinition.description = description
        pluginBundle.description = description
    }

    fun gitHub(gitHub: String) {
        pluginBundle.website = gitHub
        pluginBundle.vcsUrl = gitHub
    }

    fun tags(vararg tags: String) {
        pluginBundle.tags = tags.toList()
    }
}