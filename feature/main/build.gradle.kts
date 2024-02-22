plugins {
    id("lifemash.android.feature")
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
}
