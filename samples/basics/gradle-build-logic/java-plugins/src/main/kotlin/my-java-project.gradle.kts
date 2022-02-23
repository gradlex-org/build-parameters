plugins {
    id("java-library")
    id("de.benediktritter.build-params")
}

val host = buildParameters.dbHost
println(host)
val tomcatHome = buildParameters.tomcatHome
println(tomcatHome)