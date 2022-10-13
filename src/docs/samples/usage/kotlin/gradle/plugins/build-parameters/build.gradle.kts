// tag::plugin-application[]
plugins {
    id("org.gradlex.build-parameters") version "1.1"
}
// end::plugin-application[]

buildParameters {
    bool("myParameter") {
        defaultValue.set(true)
    }
}
