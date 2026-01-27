plugins {
    id("org.gradlex.build-parameters") version "1.4.5"
}

buildParameters {
    pluginId("org.example.build-params")
    string("dbHost") {
        defaultValue.set("localhost")
        description.set("Define the database host")
    }

    bool("ci") {
        fromEnvironment() // -> -Pci=true, env var CI
        defaultValue.set(false)
    }
    bool("local") {
        fromEnvironment("LOCAL_RUN") // -> -Plocal=true, env var LOCAL_RUN
    }
    group("gitflow") {
        description.set("Parameters configuring the gitflow process")
        enumeration("baseBranch") {
            values.addAll("bugfix-rc", "hotfix", "integration", "main", "requires", "enum")
            defaultValue.set("bugfix-rc")
        }
    }

    group("deployment") {
        description.set("Parameters related to the deployment of the app")
        string("tomcatHome") {
            defaultValue.set("/tmp/tomcat")
            description.set("Define the installation directory of the local Tomcat server")
        }
        group("dev") {
            bool("debug")
        }
    }
    group("dev") {
        group("local") {
            bool("debug")
        }
    }
}
