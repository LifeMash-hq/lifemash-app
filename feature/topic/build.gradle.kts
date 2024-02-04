plugins {
    id("lifemash.android.feature")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "org.bmsk.lifemash.feature.topic"

    buildFeatures {
        dataBinding = true
    }
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
    // glide
    implementation(libs.glide)
    // navigation
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    // test
    testImplementation(libs.junit)
}
