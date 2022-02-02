pluginManagement {
    includeBuild("../../..")
}

dependencyResolutionManagement {
    repositories.gradlePluginPortal()
}

include("my-build-parameters")
include("java-plugins")