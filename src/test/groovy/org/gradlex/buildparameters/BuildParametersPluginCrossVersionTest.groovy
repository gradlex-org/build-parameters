package org.gradlex.buildparameters

import org.gradlex.buildparameters.fixture.GradleBuild
import spock.lang.AutoCleanup
import spock.lang.Specification

class BuildParametersPluginCrossVersionTest extends Specification {

    @Delegate
    @AutoCleanup
    GradleBuild build = new GradleBuild()

    File buildLogicBuildFile

    def setup() {
        buildLogicBuildFile = build.file("build-logic/build.gradle") << """
            plugins {
                id 'org.gradlex.build-parameters'
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
        """
    }

    def "works on Gradle #gradleVersion"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("stringWithDefault") {
                    defaultValue = "some value"
                }
                string("stringWithoutDefault")
                integer("intWithDefault") {
                    defaultValue = 1
                }
                integer("intWithoutDefault")
                bool("booleanWithDefault") {
                    defaultValue = true
                }
                bool("booleanWithoutDefault")
                enumeration("enumWithDefault") {
                    values = ["One", "Two", "Three"]
                    defaultValue = "One"
                }
                enumeration("enumWithoutDefault") {
                    values = ["One", "Two", "Three"]
                }
                group("group") {
                    string("stringFromEnv") {
                        fromEnvironment()
                    }
                }
            }
        """

        and:
        buildFile << """
            println buildParameters.stringWithDefault
            println buildParameters.stringWithoutDefault.get()
            println buildParameters.intWithDefault
            println buildParameters.intWithoutDefault.get()
            println buildParameters.booleanWithDefault
            println buildParameters.booleanWithoutDefault.get()
            println buildParameters.enumWithDefault
            println buildParameters.enumWithoutDefault.get()
            println buildParameters.group.stringFromEnv.get()
        """

        and:
        environment.GROUP_STRINGFROMENV = "Something from Env"

        expect:
        runner("help", "-PstringWithoutDefault=other", "-PintWithoutDefault=5", "-PbooleanWithoutDefault=true", "-PenumWithoutDefault=Two")
                .withGradleVersion(gradleVersion)
                .build()

        where:
        gradleVersion << ["7.1", "7.1.1", "7.2", "7.3.3", "7.4.2", "7.5.1"]
    }

    def "fails the build on unsupported version"() {
        when:
        def result = runner("help")
            .withGradleVersion("7.0.2")
            .buildAndFail()

        then:
        result.output.contains("Plugin requires at least Gradle 7.1")
    }
}
