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

class BuildParametersPluginErrorTest extends Specification {

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

    def "gives proper error when accessing a mandatory parameter without value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                group("db") {
                    integer("maxConnections") {
                        description = "Max Database Connections"
                        mandatory = true
                    }
                }
            }
        """
        buildFile << """
            buildParameters.db.maxConnections.get()
        """

        expect:
        buildAndFail("help").output.contains('''
        > Build parameter db.maxConnections (Max Database Connections) not set. Use one of the following:
            -Pdb.maxConnections=value (command line)
            db.maxConnections=value (in 'gradle.properties' file)
        '''.stripIndent())
    }

    def "gives proper error when accessing a mandatory parameter without value (with fromEnvironment)"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                group("db") {
                    integer("maxConnections") {
                        description = "Max Database Connections"
                        fromEnvironment()
                        mandatory = true
                    }
                }
            }
        """
        buildFile << """
            buildParameters.db.maxConnections.get()
        """

        expect:
        buildAndFail("help").output.contains('''
        > Build parameter db.maxConnections (Max Database Connections) not set. Use one of the following:
            -Pdb.maxConnections=value (command line)
            db.maxConnections=value (in 'gradle.properties' file)
            DB_MAXCONNECTIONS=value (environment variable)
        '''.stripIndent())
    }

    def "gives proper error when accessing a mandatory parameter without value (with custom fromEnvironment)"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                group("db") {
                    integer("maxConnections") {
                        description = "Max Database Connections"
                        fromEnvironment("MAX_DB_CON")
                        mandatory = true
                    }
                }
            }
        """
        buildFile << """
            buildParameters.db.maxConnections.get()
        """

        expect:
        buildAndFail("help").output.contains('''
        > Build parameter db.maxConnections (Max Database Connections) not set. Use one of the following:
            -Pdb.maxConnections=value (command line)
            db.maxConnections=value (in 'gradle.properties' file)
            MAX_DB_CON=value (environment variable)
        '''.stripIndent())
    }
}
