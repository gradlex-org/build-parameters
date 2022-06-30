import org.gradlex.pluginpublishconventions.PluginPublishConventionExtension

plugins {
    id("com.gradle.plugin-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val pluginPublishConvention = extensions.create<PluginPublishConventionExtension>(
    "pluginPublishConvention", project, gradlePlugin, pluginBundle
)

publishing.publications.withType<MavenPublication>().all {
    pom.name.set(pluginPublishConvention.displayName)
    pom.description.set(pluginPublishConvention.description)
    pom.url.set(pluginPublishConvention.gitHub)
    pom.licenses {
        // License could be configurable
        license {
            name.set("Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
        }
    }
    pom.developers {
        // Developers could be configurable
        developer {
            id.set("britter")
            name.set("Benedikt Ritter")
            email.set("benedikt@gradlex.org")
        }
        developer {
            id.set("jjohannes")
            name.set("Jendrik Johannes")
            email.set("jendrik@gradlex.org")
        }
    }
    pom.scm {
        url.set(pluginPublishConvention.gitHub)
    }
}
