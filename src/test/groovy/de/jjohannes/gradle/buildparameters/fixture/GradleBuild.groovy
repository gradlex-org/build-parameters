package de.jjohannes.gradle.buildparameters.fixture

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
        GradleRunner.create()
                .withProjectDir(projectDir.dir)
                .withPluginClasspath()
                .withArguments(args)
                .forwardOutput()
                .withDebug(ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0)
    }

    def close() {
        projectDir.delete()
    }
}
