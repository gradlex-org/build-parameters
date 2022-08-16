plugins {
    id("org.asciidoctor.jvm.convert")
}

tasks {
    asciidoctor {
        notCompatibleWithConfigurationCache("See https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/564")

        attributes(mapOf(
            "docinfodir" to "src/docs/asciidoc",
            "docinfo" to "shared",
            "source-highlighter" to "prettify",
            "tabsize" to "4",
            "toc" to "left",
            "icons" to "font",
            "sectanchors" to true,
            "idprefix" to "",
            "idseparator" to "-"
        ))
    }
}
