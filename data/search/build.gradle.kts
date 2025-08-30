plugins {
    id("lifemash.android.data")
}

android {
    namespace = "org.bmsk.lifemash.data.search"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.domain.featureSearch)
}
