plugins {
    id("lifemash.android.feature")
}

android {
    namespace = "org.bmsk.lifemash.feature.webview"
}

dependencies {
    implementation(projects.feature.webviewApi)
    implementation(projects.feature.mainNavGraph)

    implementation(libs.kotlinx.immutable)
}
