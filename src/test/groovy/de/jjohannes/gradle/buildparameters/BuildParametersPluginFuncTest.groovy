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
            buildParameters {}
        """
    }

    def "supports string build parameters with default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("myParameter") {
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

    def "supports string build parameters without default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("myParameter") {
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

    def "supports integer build parameters with default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                integer("myParameter") {
                    description = "A simple integer parameter"
                    defaultValue = 2
                }
            }
        """
        buildFile << """
            assert buildParameters.myParameter + 2 == 4
            println "Parameter value: \${buildParameters.myParameter}"
        """

        when:
        def result = build("help")

        then:
        result.output.contains("Parameter value: 2")
    }

    def "supports integer build parameters without default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                integer("myParameter") {
                    description = "A simple integer parameter"
                }
            }
        """
        buildFile << """
            assert buildParameters.myParameter.getOrElse(2) == 2
        """

        expect:
        build("help")
    }

    def "supports boolean build parameters with default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                bool("myParameter") {
                    description = "A simple boolean parameter"
                    defaultValue = true
                }
            }
        """
        buildFile << """
            assert buildParameters.myParameter == true
            println buildParameters.myParameter
        """

        when:
        def result = build("help")

        then:
        result.output.contains("true")
    }

    def "supports boolean build parameters without default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                bool("myParameter") {
                    description = "A simple boolean parameter"
                }
            }
        """
        buildFile << """
            assert buildParameters.myParameter.getOrElse(true) == true
        """

        expect:
        build("help")
    }

    def "supports enum build parameters with default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                enumeration("myParameter") {
                    description = "A simple enum parameter"
                    values = ['One', 'Two', 'Three']
                    defaultValue = 'Two'
                }
            }
        """
        buildFile << """
            assert buildParameters.myParameter == buildparameters.MyParameter.Two
        """

        expect:
        build("help")
    }

    def "supports enum build parameters without default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                enumeration("myParameter") {
                    description = "A simple enum parameter"
                    values = ['One', 'Two', 'Three']
                }
            }
        """
        buildFile << """
            assert buildParameters.myParameter.getOrElse(buildparameters.MyParameter.Three) == buildparameters.MyParameter.Three
        """

        expect:
        build("help")
    }

    def "parameters can be grouped"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                group("db") {
                    string("host") {
                        defaultValue = "localhost"
                    }
                    string("port") {
                        defaultValue = "5432"
                    }
                }
            }
        """
        buildFile << """
            println "db.host: " + buildParameters.db.host
            println "db.port: " + buildParameters.db.port
        """

        when:
        def result = build("help", "-Pdb.port=9999")

        then:
        result.output.contains("db.host: localhost")
        result.output.contains("db.port: 9999")
    }

    def "several subgroups can have the same name"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                group("db") {
                    group("connection") {
                        string("host") {
                            defaultValue = "db host"
                        }
                    }
                }
                group("server") {
                    group("connection") {
                        string("host") {
                            defaultValue = "server host"
                        }
                    }
                }
            }
        """
        buildFile << """
            println "db.connection.host: " + buildParameters.db.connection.host
            println "server.connection.host: " + buildParameters.server.connection.host
        """

        when:
        def result = build("help", "-Pdb.port=9999")

        then:
        result.output.contains("db.connection.host: db host")
        result.output.contains("server.connection.host: server host")
    }

    def "value of build parameters cannot be changed"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("myParameter") {
                    description = "A simple string parameter"
                    defaultValue = "fooDefault"
                }
                string("myParameterOptional") {
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
    // - Error handling for unknown types added directly to parameters list
    // - Environment variable
    // - Enum with same names collide (solve or better error)
}
