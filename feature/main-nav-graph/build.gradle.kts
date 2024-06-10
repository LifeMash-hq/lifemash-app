plugins {
    id("lifemash.android.no-hilt-library")
    id("lifemash.android.compose")
}

android {
    namespace = "org.bmsk.lifemash.feature.nav"
}

dependencies {
    implementation(libs.androidx.compose.navigation)
}