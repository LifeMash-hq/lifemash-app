plugins {
    id("lifemash.android.ui")
}

android {
    namespace = "org.bmsk.lifemash.scrap.ui"
}

dependencies {
    implementation(projects.feature.scrap.api)
    implementation(projects.feature.scrap.domain)
    implementation(projects.shared.navigation)

    implementation(libs.kotlinx.immutable)
}
