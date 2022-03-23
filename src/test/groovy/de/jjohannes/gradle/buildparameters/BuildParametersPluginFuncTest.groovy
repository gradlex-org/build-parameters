package de.jjohannes.gradle.buildparameters

import de.jjohannes.gradle.buildparameters.fixture.GradleBuild
import spock.lang.AutoCleanup
import spock.lang.Specification

class BuildParametersPluginFuncTest extends Specification {

    @Delegate
    @AutoCleanup
    GradleBuild build = new GradleBuild()

    File buildLogicBuildFile

    def setup() {
        buildLogicBuildFile = build.file("build-logic/build.gradle") << """
            plugins {
                id 'de.jjohannes.gradle.build-parameters'
            }
        """
        settingsFile << """
            pluginManagement {
                includeBuild("build-logic")
            }
        """
        buildFile << """
            plugins {
                id 'build-parameters'
            }
            buildParameters {}
        """
    }

    def "supports build parameters with default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                parameter("myParameter") {
                    description = "A simple string parameter"
                    defaultValue = "foo"
                }
            }
        """
        buildFile << """
            println buildParameters.myParameter
        """

        when:
        def result = build("help")

        then:
        result.output.contains("foo")
    }

    def "supports build parameters without default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                parameter("myParameter") {
                    description = "A simple string parameter"
                }
            }
        """
        buildFile << """
            println "myParameter: " + buildParameters.myParameter.present
        """

        when:
        def result = build("help")

        then:
        result.output.contains("myParameter: false")
    }

    def "value of build parameters cannot be changed"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                parameter("myParameter") {
                    description = "A simple string parameter"
                    defaultValue = "fooDefault"
                }
                parameter("myParameterOptional") {
                    description = "A simple string parameter"
                }
            }
        """
        buildFile << """
            println "myParameter: " + buildParameters.myParameter
            myParameter = "bar"
            println "myParameter: " + buildParameters.myParameter
          
            println "myParameterOptional: " + buildParameters.myParameterOptional.get()
            myParameterOptional = "bar"
            println "myParameterOptional: " + buildParameters.myParameterOptional.get()
        """

        when:
        def result = build("help", "-PmyParameter=foo", "-PmyParameterOptional=foo")

        then:
        result.output.count("myParameter: foo") == 2
        result.output.count("myParameterOptional: foo") == 2
    }

    def "plugin id of generated plugin can be configured"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                pluginId("de.benediktritter.build-params")
            }
        """
        buildFile.text = buildFile.text.replace("build-parameters", "de.benediktritter.build-params")

        expect:
        build("help")
    }

    // Missing Features
    // - Help task for descriptions
    // - Unknown parameter detection
    // - Different Parameter Types
    // - Grouping
    // - Environment variable
}
