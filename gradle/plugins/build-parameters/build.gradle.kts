plugins {
    id("org.gradlex.build-parameters") version "1.4.4"
}

buildParameters {
    pluginId("gradlexbuild.build-parameters")
    bool("ci") {
        description.set("Whether or not the build is running in a CI environment")
        fromEnvironment()
        defaultValue.set(false)
    }
    group("signing") {
        // key and passphrase need default values because SigningExtension.useInMemoryPgpKeys does not accept providers
        description.set("Details about artifact signing")
        string("key") {
            description.set("The ID of the PGP key to use for signing artifacts")
            fromEnvironment()
            defaultValue.set("UNSET")
        }
        string("passphrase") {
            description.set("The passphrase for the PGP key specified by signing.key")
            fromEnvironment()
            defaultValue.set("UNSET")
        }
    }
    group("pluginPortal") {
        // The publish-plugin reads these values directly from System.env. We model them here
        // for completeness and documentation purposes.
        description.set("Credentials for publishing to the plugin portal")
        string("key") {
            description.set("The Plugin portal key for publishing the plugin")
            fromEnvironment("GRADLE_PUBLISH_KEY")
        }
        string("secret") {
            description.set("The Plugin portal secret for publishing the plugin")
            fromEnvironment("GRADLE_PUBLISH_SECRET")
        }
    }
}
