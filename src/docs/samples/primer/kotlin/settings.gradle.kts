pluginManagement {
    includeBuild("gradle/plugins")
}

plugins {
    id("build-parameters")
}

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://repo.my-company.com")
            credentials {
                // username has a default and is therefore of type String
                username = the<BuildParametersExtension>().deployment.username
                // [Groovy DSL] buildParameters.deployment.username

                // password does not have a default and is therefore of type Provider<String>
                password = the<BuildParametersExtension>().deployment.password.get()
                // [Groovy DSL] buildParameters.deployment.password.get()
            }
        }
    }
}
