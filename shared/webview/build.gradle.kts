plugins {
    id("lifemash.android.ui")
}

android {
    namespace = "org.bmsk.lifemash.feature.shared.webview"
}

dependencies {
    implementation(projects.shared.navigation)

    implementation(libs.kotlinx.immutable)
}
