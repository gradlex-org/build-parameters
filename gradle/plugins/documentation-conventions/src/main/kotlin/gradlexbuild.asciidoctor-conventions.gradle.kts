import org.asciidoctor.gradle.base.log.Severity

plugins {
    id("org.asciidoctor.jvm.convert")
}

tasks {
    asciidoctor {
        notCompatibleWithConfigurationCache("See https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/564")

        failureLevel = Severity.WARN

        attributes(mapOf(
            "docinfodir" to "src/docs/asciidoc",
            "docinfo" to "shared",
            "source-highlighter" to "prettify",
            "tabsize" to "4",
            "toc" to "left",
            "icons" to "font",
            "sectanchors" to true,
            "idprefix" to "",
            "idseparator" to "-",
            "samples-path" to "$projectDir/src/docs/samples"
        ))

        inputs.dir("src/docs/samples")
                .withPathSensitivity(PathSensitivity.RELATIVE)
                .withPropertyName("samples")
    }
}
