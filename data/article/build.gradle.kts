plugins {
    id("lifemash.android.data")
}

android {
    namespace = "org.bmsk.lifemash.data.article"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.domain.featureFeed)
    testImplementation(projects.core.model)
}