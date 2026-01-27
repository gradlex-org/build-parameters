plugins {
    id("org.gradlex.build-parameters") version "1.4.5"
}

// tag::environment-variable[]
buildParameters {
    bool("ci") {
        fromEnvironment()
        defaultValue.set(false)
    }
}
// end::environment-variable[]

// tag::grouped-environment-variable[]
buildParameters {
    group("myGroup") {
        string("someString") {
            fromEnvironment()
        }
    }
}
// end::grouped-environment-variable[]

// tag::custom-environment-variable[]
buildParameters {
    group("someGroup") {
        string("someString") {
            fromEnvironment("SOME_CUSTOM_ENV_VAR")
        }
    }
}
// end::custom-environment-variable[]
