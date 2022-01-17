import de.jjohannes.gradle.buildparameters.BuildParameters
import de.jjohannes.gradle.buildparameters.BuildParameter
import org.gradle.api.provider.Property

@BuildParameters(prefix = "buildP")
interface MyParameters {

    @get:BuildParameter(
        defaultValue = "localhost",
        description = "Define the database host"
    )
    val dbHost : Property<String>

    @get:BuildParameter(
        defaultValue = "123",
        description = "Define the database port"
    )
    val dbPort : Property<Int>

    @get:BuildParameter(
        defaultValue = "false",
        description = "Define the database port"
    )
    val ci : Property<Boolean>
}