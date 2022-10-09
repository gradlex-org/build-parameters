plugins {
    id("jvm-test-suite")
}

val testSamples by testing.suites.registering(JvmTestSuite::class) {
    useJUnit()
    dependencies {
        implementation("org.gradle.exemplar:samples-check:1.0.0")
        runtimeOnly(project.dependencies.gradleTestKit()) {
            because("It's required by GradleSamplesRunner but since gradleTestKit synthetic dependency it's not contained in sample-check's dependency metadata")
        }
    }
    targets.all {
        testTask {
            inputs.dir("src/docs/samples")
                .withPathSensitivity(PathSensitivity.RELATIVE)
                .withPropertyName("samples")
        }
    }
}

tasks.named("check") {
    dependsOn(testSamples)
}
