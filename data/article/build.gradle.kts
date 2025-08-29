plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "org.bmsk.lifemash.data.article"
}

dependencies {

    implementation(projects.data.core)
    implementation(projects.core.network)
    implementation(projects.domain.core)
    implementation(projects.domain.featureFeed)
}