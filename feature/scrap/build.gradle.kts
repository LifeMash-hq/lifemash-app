plugins {
    id("lifemash.android.feature")
}

android {
    namespace = "org.bmsk.lifemash.feature.scrap"
}

dependencies {
    implementation(projects.feature.scrapApi)
    implementation(projects.feature.mainNavGraph)

    implementation(libs.kotlinx.immutable)
}
