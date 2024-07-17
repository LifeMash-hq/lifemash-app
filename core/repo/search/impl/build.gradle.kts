plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "org.bmsk.lifemash.core.repo.search"
}

dependencies {
    implementation(projects.core.repo.search.api)
    implementation(projects.core.model)
    implementation(projects.core.network)
}
