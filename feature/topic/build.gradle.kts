plugins {
    id("lifemash.android.feature")
}

android {
    namespace = "org.bmsk.lifemash.feature.topic"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.androidx.databinding)
    implementation(libs.androidx.lifecycle)
    implementation(libs.material)
    // jsoup
    implementation(libs.jsoup)
    // lottie
    implementation(libs.lottie)
    implementation(libs.lottie.compose)
    // glide
    implementation(libs.glide)
    // navigation
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    implementation(libs.kotlinx.immutable)

    implementation(libs.compose.shimmer)
    implementation(libs.landscapist.bom)
    implementation(libs.landscapist.coil)
    implementation(libs.landscapist.placeholder)
}
