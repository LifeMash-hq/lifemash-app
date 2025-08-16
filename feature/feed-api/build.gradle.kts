plugins {
    id("lifemash.android.no-hilt-library")
    id("lifemash.android.compose")
}

android {
    namespace = "org.bmsk.lifemash.feature.feed.api"
}

dependencies {
    implementation(projects.feature.mainNavGraph)
    implementation(libs.androidx.compose.navigation)
}