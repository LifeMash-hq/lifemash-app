import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("lifemash.android.ui")
}

extensions.configure<LibraryExtension> {
    namespace = "org.bmsk.lifemash.feature.shared.common"
}

dependencies {
}
