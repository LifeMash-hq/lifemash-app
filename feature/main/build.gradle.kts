plugins {
    id("lifemash.android.feature")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "org.bmsk.lifemash.feature.main"

    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(projects.feature.topic)
    implementation(projects.feature.all)

    // navigation
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    // test
    testImplementation(libs.junit)
}
