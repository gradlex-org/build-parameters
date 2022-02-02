plugins {
    id("my-java-project")
}






// -PbuildParameter.dbPort=
// BUILD_PARAMETER_DB_PORT -- BUILD_P_


// Variante A
/*buildParameters.add<MyParameters>(prefix = "buildP") {
    dbHost.convention("localhost")
    dbPort.convention(123)
    ci.convention(false)
}*/

// Variante B
// extensions.create<MyParameters>("params")

/*.apply {
    dbHost.convention("localhost").finalizeValue()
    dbPort.convention(123).finalizeValue()
    ci.convention(false).finalizeValue()
}*/


