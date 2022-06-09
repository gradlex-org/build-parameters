/*
 * Copyright 2022 the original author or authors.
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
