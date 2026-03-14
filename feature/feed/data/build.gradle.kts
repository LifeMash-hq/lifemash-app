import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("lifemash.android.data")
    id("com.google.devtools.ksp")
}

extensions.configure<LibraryExtension> {
    namespace = "org.bmsk.lifemash.feed.data"
}

dependencies {
    implementation(projects.shared.network)
    implementation(projects.feature.feed.domain)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
