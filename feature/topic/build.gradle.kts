plugins {
    id("lifemash.android.feature")
}

android {
    namespace = "org.bmsk.lifemash.feature.topic"
}

dependencies {
    implementation(projects.feature.topicApi)
    implementation(projects.feature.mainNavGraph)
    implementation(projects.core.repo.scrap.api)
    implementation(projects.core.repo.search.api)
    // jsoup
    implementation(libs.jsoup)
    // lottie
    implementation(libs.lottie)
    implementation(libs.lottie.compose)
    // glide
    implementation(libs.glide)

    implementation(libs.kotlinx.immutable)

    implementation(libs.compose.shimmer)
    implementation(libs.landscapist.bom)
    implementation(libs.landscapist.coil)
    implementation(libs.landscapist.placeholder)
}
