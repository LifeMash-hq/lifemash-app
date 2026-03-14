plugins {
    id("lifemash.android.feature.ui")
}

android {
    namespace = "org.bmsk.lifemash.main"
}

dependencies {
    implementation(projects.shared.common)
    implementation(projects.shared.navigation)
    implementation(projects.shared.webview)
    implementation(projects.feature.feed.api)
    implementation(projects.feature.scrap.api)

    implementation(libs.androidx.navigation.ui.ktx)
}
