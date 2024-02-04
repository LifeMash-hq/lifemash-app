plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "org.bmsk.lifemash.core.data"
}

dependencies {
    implementation(projects.core.network)
    implementation(projects.core.model)
    implementation(projects.core.domain)

    // retrofit
    implementation(libs.retrofit)

    // jsoup
    implementation(libs.jsoup)

    // test
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
}
