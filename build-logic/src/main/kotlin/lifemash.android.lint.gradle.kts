import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint

val lint = extensions.findByType<ApplicationExtension>()?.lint
    ?: extensions.findByType<LibraryExtension>()?.lint

lint?.configure()

fun Lint.configure() {
    xmlReport = true
    sarifReport = true
    checkDependencies = true
    disable += "GradleDependency"
}
