plugins {
    id("build-parameters")
}

// tag::grouping[]
println(buildParameters.myGroup.myString.get())
println(buildParameters.myGroup.myInt.get())
// end::grouping[]
