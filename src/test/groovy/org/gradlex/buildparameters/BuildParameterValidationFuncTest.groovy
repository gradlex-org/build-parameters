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

class BuildParameterValidationFuncTest extends Specification {

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

    def "fails for build parameters that are not defined"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("param1") 
                integer("param2")
            }
        """

        expect:
        buildAndFail("help", "-Pparam3=smth").output.contains("Unknown build parameter: param3")
    }

    def "fails for build parameters that are accidentally defined via -D"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("param1") 
                integer("param2")
            }
        """

        expect:
        buildAndFail("help", "-Dparam1=smth").output.contains("Build parameter defined via '-Dparam1'! Use '-Pparam1' instead")
    }

    def "build parameter validation can be disabled"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                enableValidation = false
                string("param1") 
                integer("param2")
            }
        """

        expect:
        build("help", "-Pparam3=smth", "-Dparam1=smth") // does not fail
    }

    def "does not validate during idea sync"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("param1") 
            }
        """

        expect:
        // -Didea.version to simulate IDEA Sync
        build("help", "-Didea.version=3", "-Dandroid.injected.build.model.only.advanced=true")
    }
}
