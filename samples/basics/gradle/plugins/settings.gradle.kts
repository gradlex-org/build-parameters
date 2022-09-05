pluginManagement {
    includeBuild("../../..")
}

dependencyResolutionManagement {
    repositories.gradlePluginPortal()
}

include("build-parameters")
include("java-plugins")