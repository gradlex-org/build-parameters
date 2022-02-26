package de.jjohannes.gradle.buildparameters.fixture

import java.nio.file.Files

class Directory {

    final File dir

    Directory(File dir) {
        this.dir = dir
        assert "Unable to create directory": dir.mkdirs()
    }

    File file(String path) {
        def file = new File(dir, path)
        file.parentFile.mkdirs()
        file
    }

    Directory dir(String path) {
        def dir = new File(dir, path).tap { it.mkdirs() }
        new Directory(dir)
    }

    def delete() {
        Files.walk(dir.toPath())
                .sorted(Comparator.reverseOrder())
                .forEach { Files.delete(it) }
    }
}
