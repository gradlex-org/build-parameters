plugins {
    id("de.jjohannes.gradle.build-parameters")
}

buildParameters {
    pluginId("de.benediktritter.build-params")
    string("dbHost") {
        defaultValue.set("localhost")
        description.set("Define the database host")
    }
    string("tomcatHome") {
        defaultValue.set("/tmp/tomcat")
        description.set("Define the installation directory of the local Tomcat server")
    }
    bool("ci") {
        fromEnvironmentVariable() // -> -Pci=true, env var CI
    }
    bool("local") {
        fromEnvironmentVariable("LOCAL_RUN") // -> -Plocal=true, env var LOCAL_RUN
        fromSystemProperty() // -> -Dlocal=true
    }
}
