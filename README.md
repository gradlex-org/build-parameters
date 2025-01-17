# Build Parameters Gradle plugin

[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Fgradlex-org%2Fbuild-parameters%2Fbadge%3Fref%3Dmain&style=flat)](https://actions-badge.atrox.dev/gradlex-org/build-parameters/goto?ref=main)
[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?label=Plugin%20Portal&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Forg%2Fgradlex%2Fbuild-parameters%2Forg.gradlex.build-parameters.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/org.gradlex.build-parameters)

Compile-safe access to parameters supplied to a Gradle build.
Compatible with Java 8 and Gradle 7.1 or later.

## Primer

Describe build parameters using a rich DSL:

```kotlin
plugins {
    id("org.gradlex.build-parameters")
}

buildParameters {
    group("deployment") {
        string("username") {
            description.set("The username used for deploying to the artifact repository")
            defaultValue.set("deployer")
        }
        string("password") {
            description.set("The password used for deploying to the artifact repository")
        }
    }
}
```

Use compile-safe accessor in you build scripts to access parameter values:

```kotlin
plugins {
    id("build-parameters")
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.my-company.com")
            credentials {
                // username has a default and is therefore of type String
                username = buildParameters.deployment.username
                // password does not have a default and is therefore of type Provider<String>
                password = buildParameters.deployment.password.get()
            }
        }
    }
}
```

Run your build and pass the parameters to it using `-P` commandline parameters:

```shell
./gradlew :publish -Pdeployment.username="jane" -Pdeployment.password="super-secret"
```

Or explore available parameters by running the _parameters_ task:

![running parameters task](src/docs/asciidoc/images/primer.gif)

## Usage

See the plugin's [documentation page](https://gradlex.org/build-parameters) for more details on how to configure your build.

## Example

You can find a basic example project setup here: [samples/basics](samples/basics)

## Disclaimer

Gradle and the Gradle logo are trademarks of Gradle, Inc.
The GradleX project is not endorsed by, affiliated with, or associated with Gradle or Gradle, Inc. in any way.
