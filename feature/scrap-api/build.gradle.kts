plugins {
    id("lifemash.android.no-hilt-library")
    id("lifemash.android.compose")
}

android {
    namespace = "org.bmsk.lifemash.feature.scrap.api"
}

dependencies {
    implementation(libs.androidx.compose.navigation)
}