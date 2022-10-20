plugins {
    id("org.gradlex.build-parameters") version "1.2"
}

buildParameters {
    group("deployment") {
        string("username") {
            description.set("The username used for deploying to the artifact repository")
            defaultValue.set("deployer")
        }
        string("password") {
            description.set("The password used for deploying to the artifact repository")
        }
    }
}
