plugins {
    id("java-gradle-plugin")
    id("maven-publish")
}

group = "de.jjohannes.gradle"

gradlePlugin {
    plugins.create(project.name) {
        id = "${project.group}.${project.name}"
        implementationClass = "${project.group}.buildparameters.BuildParametersPlugin"
    }
}
