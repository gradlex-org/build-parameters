pluginManagement {
    includeBuild("gradle/plugins")
}

plugins {
    id("com.gradle.enterprise") version("3.10.2")
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.7.2"
}

dependencyResolutionManagement {
    repositories.gradlePluginPortal()
}

if ("CI" in System.getenv()) {
    gradleEnterprise {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
