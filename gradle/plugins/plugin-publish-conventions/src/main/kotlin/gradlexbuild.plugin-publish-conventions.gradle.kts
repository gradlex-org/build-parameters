import gradlexbuild.pluginpublishconventions.PluginPublishConventionsExtension

plugins {
    id("com.gradle.plugin-publish")
    id("signing")
}

val pluginPublishConventions = extensions.create<PluginPublishConventionsExtension>(
    PluginPublishConventionsExtension.NAME, project, gradlePlugin, pluginBundle, publishing.publications
)

publishing.publications.withType<MavenPublication>().configureEach {
    pom.name.set(pluginPublishConventions.displayName)
    pom.description.set(pluginPublishConventions.description)
    pom.url.set(pluginPublishConventions.gitHub)
    pom.licenses {
        // License could be configurable
        license {
            name.set("Apache-2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
        }
    }
    pom.scm {
        url.set(pluginPublishConventions.gitHub)
    }
}
