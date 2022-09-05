plugins {
    id("my-java-library")
}

if (buildParameters.ci) {
    println("Running on CI!")
}
