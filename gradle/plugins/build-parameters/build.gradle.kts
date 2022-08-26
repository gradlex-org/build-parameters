plugins {
    id("org.gradlex.build-parameters") version "1.1"
}

buildParameters {
    pluginId("gradlexbuild.build-parameters")
    bool("ci") {
        fromEnvironment()
        defaultValue.set(false)
    }
}
