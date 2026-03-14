plugins {
    id("lifemash.android.feature")
}

android {
    namespace = "org.bmsk.lifemash.feature.feed"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.domain.core)
    implementation(projects.domain.featureFeed)
    implementation(projects.domain.featureHistory)
    implementation(projects.domain.featureScrap)
    implementation(projects.feature.feedApi)
    implementation(projects.feature.mainNavGraph)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.kotlinx.immutable)
    implementation(libs.androidx.compose.material.icons.extended)
}
