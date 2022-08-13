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

import spock.lang.Specification

class IdentifierTest extends Specification {

    def "does not accept empty strings"() {
        when:
        new Identifier([]).append(" ")

        then:
        thrown(IllegalArgumentException)
    }

    def "id without group can be converted"() {
        given:
        def id = new Identifier([]).append("connection")

        expect:
        id.toFieldName() == "connection"
        id.toFullQualifiedTypeName() == "buildparameters.Connection"
        id.toPackageFolderPath() == "buildparameters/"
        id.toPackageName() == "buildparameters"
        id.toPropertyPath() == "connection"
        id.toSimpleTypeName() == "Connection"
        id.toEnvironmentVariableName() == "CONNECTION"
    }

    def "id with group can be converted"() {
        given:
        def id = new Identifier([]).append("db").append("connection")

        expect:
        id.toFieldName() == "connection"
        id.toFullQualifiedTypeName() == "buildparameters.db.Connection"
        id.toPackageFolderPath() == "buildparameters/db"
        id.toPackageName() == "buildparameters.db"
        id.toPropertyPath() == "db.connection"
        id.toSimpleTypeName() == "Connection"
        id.toEnvironmentVariableName() == "DB_CONNECTION"
    }
}
