plugins {
    id("com.gradle.enterprise") version("3.8.1")
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.6.3"
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
