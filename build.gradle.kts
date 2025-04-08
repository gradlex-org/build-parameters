plugins {
    id("groovy")
    id("gradlexbuild.build-parameters")
    id("gradlexbuild.documentation-conventions")
    id("org.gradlex.internal.plugin-publish-conventions") version "0.6"
}

group = "org.gradlex"
version = "1.4.5"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(8)
}

dependencies.constraints {
    checkstyle("com.google.guava:guava:33.4.7-jre") {
        because("CVE-2023-2976, CVE-2020-8908")
    }
    testSamplesImplementation("commons-io:commons-io:2.18.0") {
        because("CVE-2024-47554, CVE-2021-29425")
    }
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
        id = "britter"
        name = "Benedikt Ritter"
        email = "benedikt@gradlex.org"
    }
    developer {
        id = "jjohannes"
        name = "Jendrik Johannes"
        email = "jendrik@gradlex.org"
    }
}

testing.suites.named<JvmTestSuite>("test") {
    useJUnitJupiter()
    dependencies {
        implementation("org.spockframework:spock-core:2.3-groovy-3.0")
    }
    targets.all {
        testTask { maxParallelForks = 4 }
    }
}

tasks.publishPlugins {
    dependsOn(tasks.check)
}

