package de.jjohannes.gradle.buildparameters

import de.jjohannes.gradle.buildparameters.fixture.GradleBuild
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.AutoCleanup
import spock.lang.Specification

class BuildParametersPluginBasicFuncTest extends Specification {

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
                string("myParameter") {
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

}
