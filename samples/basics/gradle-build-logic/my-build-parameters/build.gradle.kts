plugins {
    id("de.jjohannes.gradle.build-parameters")
}

buildParameters {
   parameters.create("dbHost") {
        defaultValue.set("localhost")
        description.set("Define the database host")
        //default(buildParameters["dbHost2"])
    }
}
