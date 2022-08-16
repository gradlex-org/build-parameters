plugins {
    id("checkstyle")
    id("groovy")
    id("gradlexbuild.documentation-conventions")
    id("gradlexbuild.plugin-publish-conventions")
}

group = "org.gradlex"
version = "1.1"

pluginPublishConventions {
    id("${project.group}.${project.name}")
    implementationClass("org.gradlex.buildparameters.BuildParametersPlugin")
    displayName("Build Parameters Gradle Plugin")
    description("Compile-safe access to parameters supplied to a Gradle build.")
    tags("gradlex", "parameters", "build parameters")
    gitHub("https://github.com/gradlex-org/build-parameters")
}

dependencies {
    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    maxParallelForks = 4
}

checkstyle {
    configDirectory.set(layout.projectDirectory.dir("gradle/checkstyle"))
}
