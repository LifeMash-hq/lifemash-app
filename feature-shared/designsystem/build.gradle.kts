plugins {
    id("lifemash.android.library")
    id("lifemash.android.compose")
}

android {
    namespace = "org.bmsk.lifemash.feature.designsystem"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material.icons.extended)
}
