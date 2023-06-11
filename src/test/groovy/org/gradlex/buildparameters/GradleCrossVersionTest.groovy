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

class GradleCrossVersionTest extends Specification {

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
        gradleVersion << ["7.1.1", "7.6.1", "8.0.1", "8.2-rc-2"]
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
