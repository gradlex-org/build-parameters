plugins {
    id("org.gradlex.build-parameters") version "1.4.3"
}

// tag::disable-validation[]
buildParameters {
    enableValidation.set(false)
}
// end::disable-validation[]
