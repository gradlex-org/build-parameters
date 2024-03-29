plugins {
    id("maven-publish")
    id("build-parameters")
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.my-company.com")
            credentials {
                // username has a default and is therefore of type String
                username = buildParameters.deployment.username
                // password does not have a default and is therefore of type Provider<String>
                password = buildParameters.deployment.password.get()
            }
        }
    }
}
