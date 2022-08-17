plugins {
    id("jvm-test-suite")
}

val testSamples by testing.suites.registering(JvmTestSuite::class) {
    useJUnit()
    dependencies {
        implementation("org.gradle.exemplar:samples-check:1.0.0")
        implementation(project.dependencies.gradleTestKit())
        runtimeOnly("org.slf4j:slf4j-simple:1.7.16")
        runtimeOnly("org.junit.vintage:junit-vintage-engine:5.9.0")
    }
}

tasks.named("check") {
    dependsOn(testSamples)
}
