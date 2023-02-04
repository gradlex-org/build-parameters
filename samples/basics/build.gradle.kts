plugins {
    id("my-java-library")
}

tasks.register("parameters") {
    group = "help"
    dependsOn(gradle.includedBuild("plugins").task(":build-parameters:parameters"))
}

if (buildParameters.ci) {
    println("Running on CI!")
}
