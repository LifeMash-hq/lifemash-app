plugins {
    id("lifemash.android.library")
    id("lifemash.android.compose")
}

android {
    namespace = "org.bmsk.lifemash.core.designsystem"
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.landscapist.bom)
    implementation(libs.landscapist.coil)
    implementation(libs.landscapist.placeholder)
    implementation(libs.androidx.appcompat)
}
