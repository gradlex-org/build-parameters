plugins {
    id("org.gradlex.build-parameters") version "1.1"
}

buildParameters {
    pluginId("org.example.build-params")
    string("dbHost") {
        defaultValue.set("localhost")
        description.set("Define the database host")
    }
    string("tomcatHome") {
        defaultValue.set("/tmp/tomcat")
        description.set("Define the installation directory of the local Tomcat server")
    }
    bool("ci") {
        fromEnvironment() // -> -Pci=true, env var CI
        defaultValue.set(false)
    }
    bool("local") {
        fromEnvironment("LOCAL_RUN") // -> -Plocal=true, env var LOCAL_RUN
    }
}
