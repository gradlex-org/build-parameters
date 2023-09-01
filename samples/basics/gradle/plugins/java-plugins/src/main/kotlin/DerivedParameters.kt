import org.gradle.api.provider.Property

interface DerivedParameters {
    val fastBuild: Property<Boolean>
}