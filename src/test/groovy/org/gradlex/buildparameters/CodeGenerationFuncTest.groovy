/*
 * Copyright 2022 the GradleX team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradlex.buildparameters

import org.gradlex.buildparameters.fixture.GradleBuild
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.AutoCleanup
import spock.lang.Specification

class CodeGenerationFuncTest extends Specification {

    @Delegate
    @AutoCleanup
    GradleBuild build = new GradleBuild()

    void setup() {
        buildFile << """
            plugins {
                id 'org.gradlex.build-parameters'
            }
            
            buildParameters {
                string("myParameter") {
                    description = "A simple string parameter"
                    defaultValue = "foo"
                }
                group("myGroup") {
                    description = "Group description"
                }
            }
        """
    }

    def "build depends on generatePluginCode"() {
        when:
        def result = build("build")

        then:
        result.task(":generatePluginCode").outcome == TaskOutcome.SUCCESS
    }

    def "task is up to date when nothing changes"() {
        given:
        build("build")

        when:
        def result = build("build")

        then:
        result.task(":pluginDescriptors").outcome == TaskOutcome.UP_TO_DATE
        result.task(":generatePluginCode").outcome == TaskOutcome.UP_TO_DATE
    }

    def "changing the pluginId does not cause the task to become out of date"() {
        given:
        build("build")

        when:
        buildFile << """
            buildParameters.pluginId("custom-plugin-id")
        """

        and:
        def result = build("build")

        then:
        result.task(":pluginDescriptors").outcome == TaskOutcome.SUCCESS
        result.task(":generatePluginCode").outcome == TaskOutcome.UP_TO_DATE
    }

    def "adding a new parameter causes to task to become out of date"() {
        given:
        build("build")

        when:
        buildFile << """
            buildParameters {
                string("another")
            }
        """

        and:
        def result = build("build")

        then:
        result.task(":pluginDescriptors").outcome == TaskOutcome.UP_TO_DATE
        result.task(":generatePluginCode").outcome == TaskOutcome.SUCCESS
    }

    def "task is not cacheable"() {
        given:
        build("build", "--build-cache")

        when:
        def result = build("clean", "build", "--build-cache")

        then:
        result.task(":generatePluginCode").outcome == TaskOutcome.SUCCESS
    }

    def "task configuration cache compatible"() {
        given:
        build("build", "--configuration-cache")

        when:
        def result = build("build", "--configuration-cache")

        then:
        result.output.contains("Configuration cache entry reused.")
    }

}
