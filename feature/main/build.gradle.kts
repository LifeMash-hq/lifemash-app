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
    implementation(projects.feature.all)

    implementation(projects.feature.mainNavGraph)
    implementation(projects.feature.scrapApi)
    implementation(projects.feature.topicApi)
    implementation(projects.feature.webviewApi)

    // navigation
    implementation(libs.androidx.navigation.ui.ktx)
}
