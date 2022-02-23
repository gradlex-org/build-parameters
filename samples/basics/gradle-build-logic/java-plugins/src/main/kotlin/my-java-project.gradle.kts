plugins {
    id("java-library")
    id("my-build-params")
}

val host = buildParameters.dbHost
println(host)
val tomcatHome = buildParameters.tomcatHome
println(tomcatHome)