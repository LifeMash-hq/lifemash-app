plugins {
    id("lifemash.android.feature")
}

android {
    namespace = "org.bmsk.lifemash.feature.feed"
}

dependencies {
    implementation(projects.feature.feedApi)
    implementation(projects.feature.mainNavGraph)
    implementation(projects.core.repo.scrap.api)
    implementation(projects.core.repo.articleApi)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.kotlinx.immutable)
    implementation(libs.androidx.compose.material.icons.extended)
}
