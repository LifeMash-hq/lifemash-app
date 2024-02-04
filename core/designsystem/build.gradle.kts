plugins {
    id("lifemash.android.library")
    id("lifemash.android.compose")
}

android {
    namespace = "org.bmsk.lifemash.core.designsystem"
}

dependencies {
    implementation(libs.landscapist.bom)
    implementation(libs.landscapist.coil)
    implementation(libs.landscapist.placeholder)
}
