plugins {
    id("lifemash.android.compose.library")
}

android {
    namespace = "org.bmsk.lifemash.feature.shared.navigation"
}

dependencies {
    implementation(libs.androidx.compose.navigation)
}
