plugins {
    id("org.gradlex.build-parameters") version "1.4"
}

// tag::disable-validation[]
buildParameters {
    enableValidation.set(false)
}
// end::disable-validation[]
