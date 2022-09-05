plugins {
    id("java-library")
    id("org.example.build-params")
}

println("DBHost:      ${buildParameters.dbHost}")
println("Tomcat Home: ${buildParameters.tomcatHome}")
