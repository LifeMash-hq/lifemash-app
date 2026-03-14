import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("lifemash.android.ui")
}

extensions.configure<LibraryExtension> {
    namespace = "org.bmsk.lifemash.scrap.ui"
}

dependencies {
    implementation(projects.feature.scrap.api)
    implementation(projects.feature.scrap.domain)
    implementation(projects.shared.navigation)

    implementation(libs.kotlinx.immutable)
}
