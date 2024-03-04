import buildparameters.BuildParametersExtension

rootProject.name = "build-parameters"

pluginManagement {
    includeBuild("gradle/plugins")
}

plugins {
    id("com.gradle.enterprise") version "3.16.2"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.13"
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
