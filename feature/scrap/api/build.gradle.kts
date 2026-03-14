import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("lifemash.android.compose.library")
}

extensions.configure<LibraryExtension> {
    namespace = "org.bmsk.lifemash.scrap.api"
}

dependencies {
    implementation(projects.shared.navigation)
    implementation(libs.androidx.compose.navigation)
}
