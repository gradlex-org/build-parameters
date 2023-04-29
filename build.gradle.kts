plugins {
    id("groovy")
    id("gradlexbuild.build-parameters")
    id("gradlexbuild.documentation-conventions")
    id("org.gradlex.internal.plugin-publish-conventions") version "0.5"
    id("de.thetaphi.forbiddenapis") version "3.4"
}

group = "org.gradlex"
version = "1.4.4"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(8)
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

// TODO This needs to be included in org.gradlex.internal.plugin-publish-conventions
signing {
    useInMemoryPgpKeys(buildParameters.signing.key, buildParameters.signing.passphrase)
}

dependencies {
    testImplementation("org.spockframework:spock-core:2.1-groovy-3.0")
}

tasks.test {
    useJUnitPlatform()
    maxParallelForks = 4
}

tasks.publishPlugins {
    dependsOn(tasks.check)
}

forbiddenApis {
    // See https://github.com/policeman-tools/forbidden-apis/wiki/BundledSignatures
    bundledSignatures.add("jdk-unsafe")
}
