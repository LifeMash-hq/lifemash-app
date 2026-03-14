import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure

plugins {
    id("lifemash.android.compose.library")
}

extensions.configure<LibraryExtension> {
    namespace = "org.bmsk.lifemash.feature.designsystem"

    lint {
        disable += "DesignSystem"
    }
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material.icons.extended)
}
