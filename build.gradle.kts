plugins {
    id("groovy")
    id("gradlexbuild.documentation-conventions")
    id("org.gradlex.internal.plugin-publish-conventions") version "0.4"
}

group = "org.gradlex"
version = "1.2"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

pluginPublishConventions {
    id("${project.group}.${project.name}")
    implementationClass("org.gradlex.buildparameters.BuildParametersPlugin")
    displayName("Build Parameters Gradle Plugin")
    description("Compile-safe access to parameters supplied to a Gradle build.")
    tags("gradlex", "parameters", "build parameters")
    gitHub("https://github.com/gradlex-org/build-parameters")
    website("https://gradlex.org/build-parameters")
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

dependencies {
    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
}

tasks.test {
    useJUnitPlatform()
    maxParallelForks = 4
}
