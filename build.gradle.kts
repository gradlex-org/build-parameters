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
    checkstyle("com.google.guava:guava:33.5.0-jre") {
        because("CVE-2023-2976, CVE-2020-8908")
    }
    checkstyle("commons-beanutils:commons-beanutils:1.11.0") {
        because("CVE-2025-48734")
    }
    testSamplesImplementation("commons-io:commons-io:2.20.0") {
        because("CVE-2024-47554, CVE-2021-29425")
    }
    testSamplesImplementation("org.apache.commons:commons-lang3:3.18.0") {
        because("CVE-2025-48924")
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

