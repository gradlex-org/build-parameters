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

class EnumValuesFuncTest extends Specification {

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

    def "can use java keywords as enum values"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                enumeration("enumParam") {
                    description = "A simple enum parameter"
                    values = ['int', 'enum', 'super', 'not_a_keyword']
                    defaultValue = 'enum'
                }
            }
        """
        buildFile << """
            // build author uses the escaped identifier
            assert buildParameters.enumParam == buildparameters.EnumParam._super
            // if the value is an allowed identifier, it is not escaped
            assert buildParameters.enumParam != buildparameters.EnumParam.not_a_keyword
            
            // using the enum value "as String" you get the original (non-escapen) value
            assert buildparameters.EnumParam._super.toString() == 'super'
            assert "" + buildparameters.EnumParam._super == 'super'
            assert buildparameters.EnumParam._enum.toString() == 'enum'
            assert "" + buildparameters.EnumParam._enum == 'enum'
            assert buildparameters.EnumParam._int.toString() == 'int'
            assert "" + buildparameters.EnumParam._int == 'int'
            assert buildparameters.EnumParam.not_a_keyword.toString() == 'not_a_keyword'
            assert "" + buildparameters.EnumParam.not_a_keyword == 'not_a_keyword'      
        """

        expect:
        build("help", "-PenumParam=super") // user uses the un-escaped value
    }

    def "can use '-' or '.' in enum values"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                enumeration("enumParam") {
                    description = "A simple enum parameter"
                    values = ['a.special.value', 'fix-rc', 'combine.it_all-']
                    defaultValue = 'fix-rc'
                }
            }
        """
        buildFile << """
            // build author uses the escaped identifier
            assert buildParameters.enumParam == buildparameters.EnumParam.combine_it_all_
            assert buildParameters.enumParam != buildparameters.EnumParam.fix_rc
            assert buildParameters.enumParam != buildparameters.EnumParam.a_special_value
        """

        expect:
        build("help", "-PenumParam=combine.it_all-") // user uses the un-escaped value
    }

    // This test documents the current behavior.
    // We can think about more validation before generating code that does not compile, but that concerns many
    // features of the plugin and we should aim for a unified validation solution, if any (see #22).
    // In practice, users would probably not run into such a situation.
    def "compilation fails if two enum values escape to the same escaped value"() {
        given:
        buildLogicBuildFile << """
            buildParameters {
                enumeration("enumParam") {
                    description = "A simple enum parameter"
                    values = ['a.special.value', 'a-special-value', 'a_special_value']
                    defaultValue = 'fix-rc'
                }
            }
        """

        expect:
        buildAndFail("help").output.contains(
                "error: variable a_special_value is already defined in enum EnumParam"
        )
    }
}
