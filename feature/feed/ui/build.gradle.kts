import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("lifemash.android.ui")
}

extensions.configure<LibraryExtension> {
    namespace = "org.bmsk.lifemash.feed.ui"
}

dependencies {
    implementation(projects.feature.feed.domain)
    implementation(projects.feature.feed.api)
    implementation(projects.feature.scrap.domain)
    implementation(projects.shared.navigation)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.kotlinx.immutable)
    implementation(libs.androidx.compose.material.icons.extended)
}
