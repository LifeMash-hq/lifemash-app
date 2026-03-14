plugins {
    id("lifemash.android.data")
}

android {
    namespace = "org.bmsk.lifemash.data.search"
}

dependencies {
    implementation(projects.data.network)
    implementation(projects.domain.featureSearch)
}
