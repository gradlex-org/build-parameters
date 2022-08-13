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
        Identifier.root().append(" ")

        then:
        thrown(IllegalArgumentException)
    }

    def "can be converted to CamelCase"() {
        given:
        def id = Identifier.root().append("db").append("connection")

        expect:
        id.toCamelCase() == "DbConnection"
    }

    def "can be converted to dotted.case"() {
        given:
        def id = Identifier.root().append("db").append("connection")

        expect:
        id.toDottedCase() == "db.connection"
    }
}
