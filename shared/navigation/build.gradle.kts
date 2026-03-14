import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("lifemash.android.compose.library")
}

extensions.configure<LibraryExtension> {
    namespace = "org.bmsk.lifemash.feature.shared.navigation"
}

dependencies {
    implementation(libs.androidx.compose.navigation)
}
