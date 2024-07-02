plugins {
    id("lifemash.android.feature")
}

android {
    namespace = "org.bmsk.lifemash.feature.scrap"
}

dependencies {
    implementation(projects.feature.scrapApi)
    implementation(projects.feature.mainNavGraph)
    implementation(projects.core.repo.scrap.api)

    implementation(libs.kotlinx.immutable)
}
