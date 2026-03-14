plugins {
    id("lifemash.android.compose.library")
}

android {
    namespace = "org.bmsk.lifemash.feed.api"
}

dependencies {
    implementation(projects.shared.navigation)
    implementation(libs.androidx.compose.navigation)
}
