plugins {
    id("lifemash.android.feature.ui")
}

android {
    namespace = "org.bmsk.lifemash.feature.shared.webview"
}

dependencies {
    implementation(projects.shared.navigation)

    implementation(libs.kotlinx.immutable)
}
