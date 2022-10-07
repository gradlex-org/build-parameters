plugins {
    id("jvm-test-suite")
}

val testSamples by testing.suites.registering(JvmTestSuite::class) {
    useJUnit()
    dependencies {
        implementation("org.gradle.exemplar:samples-check:1.0.0")
        implementation(project.dependencies.gradleTestKit())
        runtimeOnly("org.slf4j:slf4j-simple:1.7.16")
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
