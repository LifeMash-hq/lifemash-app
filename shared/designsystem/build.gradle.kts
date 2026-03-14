plugins {
    id("lifemash.android.compose.library")
}

android {
    namespace = "org.bmsk.lifemash.feature.designsystem"

    lint {
        disable += "DesignSystem"
    }
}

dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material.icons.extended)
}
