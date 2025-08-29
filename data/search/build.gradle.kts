plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
}

android {
    namespace = "org.bmsk.lifemash.data.search"
}

dependencies {
    implementation(projects.data.core)
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.domain.featureSearch)
    implementation(projects.domain.core)
}
