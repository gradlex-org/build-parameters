plugins {
    id("checkstyle")
    id("groovy")
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

dependencies {
    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = 4
}
