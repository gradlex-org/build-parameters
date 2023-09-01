plugins {
    id("java-library")
    id("org.example.build-params")
}

val derivedParameters = extensions.create<DerivedParameters>("derivedParameters")

derivedParameters.fastBuild.set(buildParameters.ci || buildParameters.dbHost == "127.0.0.1")
