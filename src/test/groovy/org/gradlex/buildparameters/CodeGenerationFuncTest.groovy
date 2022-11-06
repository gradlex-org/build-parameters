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
        settingsFile << """
            buildCache.local.directory = new File(rootDir, 'build-cache')
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

    def "task is loaded from cache if nothing changes"() {
        given:
        build("build", "--build-cache")

        when:
        def result = build("clean", "build", "--build-cache")

        then:
        result.task(":generatePluginCode").outcome == TaskOutcome.FROM_CACHE
    }

    def "task is loaded from cache if only pluginId changes"() {
        given:
        build("build", "--build-cache")

        when:
        buildFile << """
            buildParameters.pluginId("custom-plugin-id")
        """
        def result = build("clean", "build", "--build-cache")

        then:
        result.task(":pluginDescriptors").outcome == TaskOutcome.SUCCESS
        result.task(":generatePluginCode").outcome == TaskOutcome.FROM_CACHE
    }

    def "task is not loaded from cache if parameters change"() {
        given:
        build("build", "--build-cache")

        when:
        buildFile << """
            buildParameters {
                string("another")
            }
        """
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

    def "it renders descriptions of parameters and groups as JavaDocs of getters"() {
        when:
        build("build")

        then:
        def parameterJavaDoc =
        """ |    /**
            |     * A simple string parameter
            |     */
            |    public String getMyParameter()""".stripMargin("|")
        generatedFile("buildparameters/BuildParametersExtension.java").text.contains(parameterJavaDoc)

        and:
        def groupJavaDoc =
        """ |    /**
            |     * Group description
            |     */
            |    public buildparameters.MyGroup getMyGroup()""".stripMargin("|")
        generatedFile("buildparameters/BuildParametersExtension.java").text.contains(groupJavaDoc)
    }

    def "it renders multiline descriptions as multi line JavaDoc comments"() {
        given:
        buildFile << """
            buildParameters {
                integer("myInt") {
                    description = ${'"""'}
                        A multi line
                        description for
                        
                        an int parameter.
                    ${'"""'}
                    defaultValue = 42
                }
                group("someGroup") {
                    description = ${'"""'}
                        A multi line
                        description for
                        
                        a group.
                    ${'"""'}
                }
            }
        """

        when:
        build("build")

        then:
        def parameterJavaDoc =
        """ |    /**
            |     * A multi line
            |     * description for
            |     * 
            |     * an int parameter.
            |     */
            |    public int getMyInt()""".stripMargin("|")
        generatedFile("buildparameters/BuildParametersExtension.java").text.contains(parameterJavaDoc)

        and:
        def groupJavaDoc =
        """ |    /**
            |     * A multi line
            |     * description for
            |     * 
            |     * a group.
            |     */
            |    public buildparameters.SomeGroup getSomeGroup()""".stripMargin("|")
        generatedFile("buildparameters/BuildParametersExtension.java").text.contains(groupJavaDoc)
    }

    private File generatedFile(String path) {
        projectDir.file("build/generated/sources/build-parameters-plugin/java/main/$path")
    }
}
