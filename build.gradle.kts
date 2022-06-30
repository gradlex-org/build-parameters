plugins {
    id("checkstyle")
    id("groovy")
    id("org.gradlex.plugin-publish-convention")
}

group = "org.gradlex"
version = "0.1"

pluginPublishConvention {
    id("${project.group}.${project.name}")
    implementationClass("de.jjohannes.gradle.buildparameters.BuildParametersPlugin")
    displayName("Build Parameters Gradle Plugin")
    description("GradleX Plugin: Compile-safe access to parameters supplied to a Gradle build.")
    tags("gradlex", "parameters", "build parameters")
    gitHub("https://github.com/gradlex-org/build-parameters")
}

dependencies {
    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = 4
}

checkstyle {
    configDirectory.set(layout.projectDirectory.dir("gradle/checkstyle"))
}
