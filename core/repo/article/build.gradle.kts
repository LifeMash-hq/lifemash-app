plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "org.bmsk.lifemash.core.repo.article"
}

dependencies {
    implementation(projects.core.repo.articleApi)
    implementation(projects.core.network)
    implementation(projects.core.model)
    implementation(projects.core.network)
}
