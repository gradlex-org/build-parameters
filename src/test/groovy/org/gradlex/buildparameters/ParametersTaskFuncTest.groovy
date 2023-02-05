package org.gradlex.buildparameters

import org.gradlex.buildparameters.fixture.GradleBuild
import spock.lang.AutoCleanup
import spock.lang.Specification

class ParametersTaskFuncTest extends Specification {

    @Delegate
    @AutoCleanup
    GradleBuild build = new GradleBuild()

    File buildLogicBuildFile

    def setup() {
        buildLogicBuildFile = build.file("build-logic/build.gradle") << """
            plugins {
                id 'org.gradlex.build-parameters'
            }
            buildParameters {
                string("dbHost") {
                    defaultValue = "localhost"
                    description = "Define the database host"
                }
            
                bool("ci") {
                    fromEnvironment()
                    defaultValue = false
                }
                bool("local") {
                    fromEnvironment("LOCAL_RUN")
                }
                group("gitflow") {
                    description = "Parameters configuring the gitflow process"
                    enumeration("baseBranch") {
                        values = ["bugfix", "hotfix", "integration", "main"]
                        defaultValue = "main"
                        fromEnvironment("GITBRANCH")
                    }
                }
            
                group("deployment") {
                    description = "Parameters related to the deployment of the app"
                    string("tomcatHome") {
                        defaultValue = "/tmp/tomcat"
                        description = "Define the installation directory of the local Tomcat server"
                    }
                    group("dev") {
                        integer("port")
                    }
                }
                group("dev") {
                    group("local") {
                        bool("debug")
                    }
                }
            }
        """
        settingsFile << """
            pluginManagement {
                includeBuild("build-logic")
            }
        """
    }

    def "renders parameter help"() {
        expect:
        build(":build-logic:parameters")
    }

    def "renders a parameter report"() {
        when:
        def result = build(":build-logic:parameters")

        then: "does contain parameters with description"
        result.output.contains("dbHost - Define the database host")

        and: "does contain grouped parameters with description"
        result.output.contains("deployment.tomcatHome - Define the installation directory of the local Tomcat server")

        and: "does contain group headers"
        result.output.contains("gitflow - Parameters configuring the gitflow process")
        result.output.contains("----------------------------------------------------")

        and: "does not contain groups having no parameters and no descriptions"
        !result.output.contains("\ndev\n");

        and: "does contain help texts"
        result.output.contains("To set a parameter use -Pparameter.name=value")
        result.output.contains("To see more detail about a parameter, run ./gradlew :build-logic:parameters --details <parameter.name>")
    }

    def "fails the build if details are requested for unknown parameter"() {
        when:
        def result = buildAndFail(":build-logic:parameters", "--details", "foo")

        then:
        result.output.contains("Unknown build parameter: foo")
    }

    def "prints details for String parameter"() {
        when:
        def result = build(":build-logic:parameters", "--details", "dbHost")

        then:
        result.output.contains("Type\n     String")
        result.output.contains("Description\n     Define the database host")
        result.output.contains("Default value\n     localhost")
        !result.output.contains("Environment Variable")
        result.output.contains("Examples\n     -PdbHost=\"a string value\"")
    }

    def "prints details for Integer parameter"() {
        when:
        def result = build(":build-logic:parameters", "--details", "deployment.dev.port")

        then:
        result.output.contains("Type\n     Integer")
        !result.output.contains("Description")
        result.output.contains("Default value\n     (none)")
        !result.output.contains("Environment Variable")
        result.output.contains("Examples\n     -Pdeployment.dev.port=42")
    }

    def "prints details for Boolean parameter"() {
        when:
        def result = build(":build-logic:parameters", "--details", "ci")

        then:
        result.output.contains("Type\n     Boolean")
        !result.output.contains("Description")
        result.output.contains("Default value\n     false")
        result.output.contains("Environment Variable\n     CI")
        result.output.contains("Examples\n     -Pci\n     -Pci=false")
    }

    def "prints details for Enum parameter"() {
        when:
        def result = build(":build-logic:parameters", "--details", "gitflow.baseBranch")

        then:
        result.output.contains("Type\n     Enum")
        result.output.contains("Values\n     bugfix, hotfix, integration, main")
        result.output.contains("Default value\n     main")
        result.output.contains("Environment Variable\n     GITBRANCH")
        result.output.contains("Examples\n     -Pgitflow.baseBranch=bugfix")
    }
}
