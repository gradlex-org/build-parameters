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

import org.gradle.testkit.runner.TaskOutcome
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

    def "can use mandatory parameter for publish repository password"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                group("deployment") {
                    string("username") {
                        defaultValue.set("test")
                    }
                    string("password") {
                        description.set("The password used for deploying to the artifact repository")
                        mandatory.set(true)
                    }
                }
            }
        """
        buildFile.text = """
            import org.gradle.internal.artifacts.repositories.AuthenticationSupportedInternal
            
            plugins {
                id 'build-parameters'
                id 'java-library'
                id 'maven-publish'
            }
            
            group = "org.test"
            version = "0.1"
            
            publishing.publications.create("main", MavenPublication).from(components.java)
            
            def c = objects.newInstance(LazyPasswordCredentials)
            c.user.set(buildParameters.deployment.username)
            c.secret.set(buildParameters.deployment.password)
            
            publishing.repositories {
                maven {
                    url = 'https://oss.sonatype.org/service/local/staging/deploy/maven2'
                    (it as AuthenticationSupportedInternal).configuredCredentials = c
                }
            }
            
            abstract class LazyPasswordCredentials implements PasswordCredentials {
                @Input
                abstract Property<String> getUser()
                @Input
                abstract Property<String> getSecret()
            
                @Internal
                String getUsername() { user.get() }
                @Internal
                String getPassword() { user.get() }
            
                void setUsername(String userName) { user.set(userName)  }
                void setPassword(String password) { user.set(password) }
            }
        """

        when:
        def assemble = build("assemble")

        then:
        assemble.task(":assemble").outcome == TaskOutcome.SUCCESS

        when:
        def publish = buildAndFail("publish")

        then:
        publish.output.contains('''
        > Failed to calculate the value of property 'secret'.
           > Build parameter deployment.password (The password used for deploying to the artifact repository) not set. Use one of the following:
               -Pdeployment.password=value (command line)
               deployment.password=value (in 'gradle.properties' file)
        '''.stripIndent())
    }
}
