plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "org.bmsk.lifemash.core.network"
}

dependencies {
    implementation(projects.core.model)

    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.moshi)

    kapt(libs.tikxml.processor)
    implementation(libs.tikxml.annotation)
    implementation(libs.tikxml.retrofit)
}
