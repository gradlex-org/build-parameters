plugins {
    id("java-library")
    id("org.example.build-params")
}

val host = buildParameters.dbHost
println(host)
val tomcatHome = buildParameters.tomcatHome
println(tomcatHome)