// tag::plugin-application[]
plugins {
    id("org.gradlex.build-parameters") version "1.0"
}
// end::plugin-application[]

buildParameters {
    bool("myParameter") {
        defaultValue.set(true)
    }
}
