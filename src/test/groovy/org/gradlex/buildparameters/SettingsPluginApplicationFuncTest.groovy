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

class SettingsPluginApplicationFuncTest extends Specification {

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
            plugins {
                id 'build-parameters'
            }
            buildParameters {}
        """
    }

    def "supports reading build parameters in settings file"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("myString") {
                    fromEnvironment()
                    defaultValue = "default my string"
                }
                integer("myInt") {
                    defaultValue = 99
                }
                bool("myBool") {
                }
                enumeration("myEnum") {
                    values = ['A', 'B']
                }
            }
        """
        settingsFile << """
            println "myString: " + buildParameters.myString
            println "myInt: " + buildParameters.myInt
            println "myBool: " + buildParameters.myBool.get()
            println "myEnum: " + buildParameters.myEnum.get()
        """

        and:
        environment.MYSTRING = "from env"

        when:
        def result = build("help", "-PmyBool=true", "-PmyEnum=B")

        then:
        result.output.contains("myString: from env")
        result.output.contains("myInt: 99")
        result.output.contains("myBool: true")
        result.output.contains("myEnum: B")
    }

    def "supports reading parameter groups in settings file"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                group("db") {
                    string("host") {
                        defaultValue = "localhost"
                    }
                    string("port") {
                    }
                }
            }
        """
        settingsFile << """
            println "db.host: " + buildParameters.db.host
            println "db.port: " + buildParameters.db.port.get()
        """

        when:
        def result = build("help", "-Pdb.port=9999")

        then:
        result.output.contains("db.host: localhost")
        result.output.contains("db.port: 9999")
    }

}
