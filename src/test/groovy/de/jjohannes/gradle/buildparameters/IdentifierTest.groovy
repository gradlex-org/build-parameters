package de.jjohannes.gradle.buildparameters

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
