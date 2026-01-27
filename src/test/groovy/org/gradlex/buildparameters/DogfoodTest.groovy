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
import spock.lang.AutoCleanup
import spock.lang.Specification

class DogfoodTest extends Specification {

    @Delegate
    @AutoCleanup
    GradleBuild build = new GradleBuild()

    def "can define build parameters in a build that has itself build parameters"() {
        given:
        build.file("build-logic/build.gradle") << """
            plugins {
                id 'org.gradlex.build-parameters'
            }
            buildParameters {
                string("parameterDesc") {
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

        when:
        buildFile << """
            plugins {
                id 'build-parameters' // generated plugin with parameters for the build
                id 'org.gradlex.build-parameters' // plugin to define parameters
            }
            buildParametersDefinition {
                string("myParameter") {
                    description = buildParameters.parameterDesc
                    defaultValue = "foo"
                }
            }
        """

        then:
        build("help")
    }
}
