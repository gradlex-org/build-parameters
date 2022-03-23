package de.jjohannes.gradle.buildparameters

import de.jjohannes.gradle.buildparameters.fixture.GradleBuild
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.AutoCleanup
import spock.lang.Specification

class BuildParametersPluginFuncTest extends Specification {

    @Delegate
    @AutoCleanup
    GradleBuild build = new GradleBuild()

    def "build depends on generatePluginCode"() {
        given:
        buildFile << """
            plugins {
                id 'de.jjohannes.gradle.build-parameters'
            }
            
            buildParameters {
                parameter("myParameter") {
                    description = "A simple string parameter"
                    defaultValue = "foo"
                }
            }
        """

        when:
        def result = build("build")

        then:
        result.task(":generatePluginCode").outcome == TaskOutcome.SUCCESS
    }

    def "supports build parameters with default value"() {
        given:
        build.file("build-logic/build.gradle") << """
            plugins {
                id 'de.jjohannes.gradle.build-parameters'
            }
            
            buildParameters {
                parameter("myParameter") {
                    description = "A simple string parameter"
                    defaultValue = "foo"
                }
            }
        """
        settingsFile << """
        pluginManagement {
            includeBuild("build-logic")
        }
        """
        buildFile << """
        plugins {
            id 'my-build-params'
        }
        
        println buildParameters.myParameter
        """

        when:
        def result = build("help")

        then:
        result.output.contains("foo")
    }

    def "supports build parameters without default value"() {
        given:
        build.file("build-logic/build.gradle") << """
            plugins {
                id 'de.jjohannes.gradle.build-parameters'
            }
            
            buildParameters {
                parameter("myParameter") {
                    description = "A simple string parameter"
                }
            }
        """
        settingsFile << """
        pluginManagement {
            includeBuild("build-logic")
        }
        """
        buildFile << """
        plugins {
            id 'my-build-params'
        }
        
        println "myParameter: " + buildParameters.myParameter.present
        """

        when:
        def result = build("help")

        then:
        result.output.contains("myParameter: false")
    }
}
