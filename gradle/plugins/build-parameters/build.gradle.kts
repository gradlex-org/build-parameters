plugins {
    id("org.gradlex.build-parameters") version "1.3"
}

buildParameters {
    pluginId("gradlexbuild.build-parameters")
    bool("ci") {
        description.set("Whether or not the build is running in a CI environment")
        fromEnvironment()
        defaultValue.set(false)
    }
}
