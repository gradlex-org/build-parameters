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
import org.gradlex.buildparameters.fixture.RestoreDefaultLocale
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Specification

class GeneratedPluginFuncTest extends Specification {

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
                group("g1") {
                    bool("myParameter") {
                        defaultValue = false
                    }
                }
                group("g2") {
                    bool("myParameter") {
                        defaultValue = true
                    }
                }
            }
        """
        buildFile << """
            assert buildParameters.myParameter == true
            assert buildParameters.g1.myParameter == true
            assert buildParameters.g2.myParameter == false
        """

        expect:
        build("help", "-Pg1.myParameter", "-Pg2.myParameter=false")
    }

    def "supports boolean build parameters without default value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                bool("myParameter") {
                    description = "A simple boolean parameter"
                }
                group("g1") {
                    bool("myParameter")
                }
                group("g2") {
                    bool("myParameter")
                }
            }
        """
        buildFile << """
            assert buildParameters.myParameter.getOrElse(true) == true
            assert buildParameters.g1.myParameter.get() == true
            assert buildParameters.g2.myParameter.get() == false
        """

        expect:
        build("help", "-Pg1.myParameter", "-Pg2.myParameter=false")
    }

    def "using an unknown value for a boolean parameter leads to an error"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                group("g1") {
                    bool("myParameter") {
                        defaultValue = false
                    }
                }
            }
        """

        when:
        def result = buildAndFail("help", "-Pg1.myParameter=treu")

        then:
        result.output.contains("Value 'treu' for parameter 'g1.myParameter' is not a valid boolean value - use 'true' (or '') / 'false'")
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

    def "several enums in different groups can have the same name"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                group("group1") {
                    enumeration("myParameter") {
                        description = "A simple enum parameter"
                        values = ['One', 'Two', 'Three']
                    }
                }
                group("group2") {
                    enumeration("myParameter") {
                        description = "Another simple enum parameter"
                        values = ['A', 'B', 'C']
                    }
                }
            }
        """
        buildFile << """
            println "group1.myParameter: " + buildParameters.group1.myParameter.get()
            println "group2.myParameter: " + buildParameters.group2.myParameter.get()
        """

        when:
        def result = build("help", "-Pgroup1.myParameter=One", "-Pgroup2.myParameter=C")

        then:
        result.output.contains("group1.myParameter: One")
        result.output.contains("group2.myParameter: C")
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
                    }
                }
            }
        """
        buildFile << """
            println "db.host: " + buildParameters.db.host
            println "db.port: " + buildParameters.db.port.get()
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
        def result = build("help")

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
                pluginId("org.example.build-params")
            }
        """
        buildFile.text = buildFile.text.replace("build-parameters", "org.example.build-params")

        expect:
        build("help")
    }

    def "parameter values without default can be derived from environment variable"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("myString") {
                    fromEnvironment()
                }
                integer("myInt") {
                    fromEnvironment()
                }
                bool("myBool") {
                    fromEnvironment()
                }
                enumeration("myEnum") {
                    fromEnvironment()
                    values = ['A', 'B']
                }
            }
        """
        buildFile << """
            println "myString: " + buildParameters.myString.get()
            println "myInt: " + buildParameters.myInt.get()
            println "myBool: " + buildParameters.myBool.get()
            println "myEnum: " + buildParameters.myEnum.get()
        """

        and:
        environment.MYSTRING = "something else"
        environment.MYINT = "13"
        environment.MYBOOL = "true"
        environment.MYENUM = "A"

        when:
        def result = build("help")

        then:
        result.output.contains("myString: something else")
        result.output.contains("myInt: 13")
        result.output.contains("myBool: true")
        result.output.contains("myEnum: A")
    }

    def "parameter values can be derived from environment variable"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("myString") {
                    fromEnvironment()
                    defaultValue = "fooDefault"
                }
                integer("myInt") {
                    fromEnvironment()
                    defaultValue = 19
                }
                bool("myBool") {
                    fromEnvironment()
                    defaultValue = true
                }
                enumeration("myEnum") {
                    fromEnvironment()
                    values = ['A', 'B']
                    defaultValue = 'B'
                }
            }
        """
        buildFile << """
            println "myString: " + buildParameters.myString
            println "myInt: " + buildParameters.myInt
            println "myBool: " + buildParameters.myBool
            println "myEnum: " + buildParameters.myEnum
        """

        and:
        environment.MYSTRING = "something else"
        environment.MYINT = "13"
        environment.MYBOOL = "false"
        environment.MYENUM = "A"

        when:
        def result = build("help")

        then:
        result.output.contains("myString: something else")
        result.output.contains("myInt: 13")
        result.output.contains("myBool: false")
        result.output.contains("myEnum: A")
    }

    def "parameter values can be derived from custom environment variable"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("myString") {
                    fromEnvironment("CUSTOM_STRING")
                    defaultValue = "fooDefault"
                }
                integer("myInt") {
                    fromEnvironment("CUSTOM_INT")
                    defaultValue = 19
                }
                bool("myBool") {
                    fromEnvironment("CUSTOM_BOOL")
                    defaultValue = true
                }
                enumeration("myEnum") {
                    fromEnvironment("CUSTOM_ENUM")
                    values = ['A', 'B']
                    defaultValue = 'B'
                }
            }
        """
        buildFile << """
            println "myString: " + buildParameters.myString
            println "myInt: " + buildParameters.myInt
            println "myBool: " + buildParameters.myBool
            println "myEnum: " + buildParameters.myEnum
        """

        and:
        environment.CUSTOM_STRING = "something else"
        environment.CUSTOM_INT = "13"
        environment.CUSTOM_BOOL = "false"
        environment.CUSTOM_ENUM = "A"

        when:
        def result = build("help")

        then:
        result.output.contains("myString: something else")
        result.output.contains("myInt: 13")
        result.output.contains("myBool: false")
        result.output.contains("myEnum: A")
    }

    def "parameter values can be derived from custom environment variable"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                group("group") {
                    string("myString") {
                        fromEnvironment()
                        defaultValue = "fooDefault"
                    }
                    integer("myInt") {
                        fromEnvironment()
                        defaultValue = 19
                    }
                    bool("myBool") {
                        fromEnvironment()
                        defaultValue = true
                    }
                    enumeration("myEnum") {
                        fromEnvironment()
                        values = ['A', 'B']
                        defaultValue = 'B'
                    }
                }
            }
        """
        buildFile << """
            println "myString: " + buildParameters.group.myString
            println "myInt: " + buildParameters.group.myInt
            println "myBool: " + buildParameters.group.myBool
            println "myEnum: " + buildParameters.group.myEnum
        """

        and:
        environment.GROUP_MYSTRING = "something else"
        environment.GROUP_MYINT = "13"
        environment.GROUP_MYBOOL = "false"
        environment.GROUP_MYENUM = "A"

        when:
        def result = build("help")

        then:
        result.output.contains("myString: something else")
        result.output.contains("myInt: 13")
        result.output.contains("myBool: false")
        result.output.contains("myEnum: A")
    }

    def "parameter values are taken from command line first"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("myString") {
                    fromEnvironment()
                }
                integer("myInt") {
                    fromEnvironment()
                }
                bool("myBool") {
                    fromEnvironment()
                }
                enumeration("myEnum") {
                    fromEnvironment()
                    values = ['A', 'B']
                }
            }
        """
        buildFile << """
            println "myString: " + buildParameters.myString.get()
            println "myInt: " + buildParameters.myInt.get()
            println "myBool: " + buildParameters.myBool.get()
            println "myEnum: " + buildParameters.myEnum.get()
        """

        and:
        environment.MYSTRING = "mystring from env"
        environment.MYINT = "13"
        environment.MYBOOL = "true"
        environment.MYENUM = "A"

        when:
        def result = build("help", "-PmyString=Something", "-PmyInt=2", "-PmyBool=false", "-PmyEnum=B")

        then:
        result.output.contains("myString: Something")
        result.output.contains("myInt: 2")
        result.output.contains("myBool: false")
        result.output.contains("myEnum: B")
    }

    @Issue("https://github.com/gradlex-org/build-parameters/issues/40")
    def "parameters can be defined without configuration closure"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                string("myString")
                integer("myInt")
                bool("myBool")
                group("myGroup") {
                    string("myString")
                    integer("myInt")
                    bool("myBool")
                }
            }
        """

        expect:
        build("help")
    }

    def "generator task is compatible with configuration cache"() {
        expect:
        build("help", "--configuration-cache")
    }


    @RestoreDefaultLocale
    @Issue("https://github.com/gradlex-org/build-parameters/issues/87")
    def "code generation is locale insensitive"() {
        given:
        Locale.setDefault(new Locale("tr", "TR"))

        and:
        buildLogicBuildFile << """
            buildParameters {
                bool("includeTestTags") {
                    defaultValue = true
                }
            }
        """

        and: "Kotlin DSL is used"
        buildFile.delete()
        build.file("build.gradle.kts") << """
            plugins {
                id("build-parameters")
            }
            println("includeTestTags is accessible: " + buildParameters.includeTestTags)
        """

        expect:
        build("help")
    }
}
