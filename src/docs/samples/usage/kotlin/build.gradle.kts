plugins {
    id("build-parameters")
}

if (buildParameters.myParameter) {
    println("myParameter was set to true")
}
