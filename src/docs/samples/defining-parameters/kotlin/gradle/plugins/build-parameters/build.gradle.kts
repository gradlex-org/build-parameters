plugins {
    id("org.gradlex.build-parameters") version "1.0"
}

// tag::string-parameter[]
buildParameters {
    string("myString") {
        description.set("Optional description of the string parameter")
        defaultValue.set("Optional default value")
    }
}
// end::string-parameter[]

// tag::int-parameter[]
buildParameters {
    integer("myInt") {
        description.set("Optional description of the int parameter")
        defaultValue.set(9) // optional
    }
}
// end::int-parameter[]

// tag::boolean-parameter[]
buildParameters {
    bool("mybool") {
        description.set("Optional description of the bool parameter")
        defaultValue.set(true) // optional
    }
}
// end::boolean-parameter[]

// tag::enum-parameter[]
buildParameters {
    enumeration("myEnum") {
        description.set("Optional description of the enum parameter")
        values.addAll("One", "Two", "Three")
        defaultValue.set("One") // optional
    }
}
// end::enum-parameter[]