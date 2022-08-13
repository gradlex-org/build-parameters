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

package org.gradlex.buildparameters.fixture

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

import java.lang.management.ManagementFactory
import java.nio.file.Files

class GradleBuild {

    @Delegate
    final Directory projectDir
    final File buildFile
    final File settingsFile
    final File gradleProperties
    final Map<String, String> environment = [:]

    GradleBuild(File projectDir = Files.createTempDirectory("gradle-build").toFile()) {
        this.projectDir = new Directory(projectDir)
        this.buildFile = new File(projectDir, "build.gradle")
        this.settingsFile = new File(projectDir, "settings.gradle")
        this.gradleProperties = new File(projectDir, "gradle.properties")
    }

    BuildResult build(String... args) {
        runner(args).build()
    }

    BuildResult buildAndFail(String... args) {
        runner(args).buildAndFail()
    }

    GradleRunner runner(String... args) {
        def runner = GradleRunner.create()
                .withProjectDir(projectDir.dir)
                .withPluginClasspath()
                .withArguments(args)
                .forwardOutput()
                .withDebug(ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0)
        if (!environment.isEmpty()) {
            runner.withEnvironment(environment)
        }
        runner
    }

    def close() {
        projectDir.delete()
    }
}
