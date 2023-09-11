import buildparameters.BuildParametersExtension

rootProject.name = "build-parameters"

pluginManagement {
    includeBuild("gradle/plugins") {
        name = "build-parameters-gradle-plugins"
    }
}

plugins {
    id("com.gradle.enterprise") version "3.14.1"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.11.2"
    id("gradlexbuild.build-parameters")
}

dependencyResolutionManagement {
    repositories.gradlePluginPortal()
}

if (the<BuildParametersExtension>().ci) {
    gradleEnterprise {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
