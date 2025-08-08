plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
    id("kotlinx-serialization")
    id("kotlin-kapt")
}

android {
    namespace = "org.bmsk.lifemash.core.network"
}

dependencies {
    implementation(projects.core.model)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    kapt(libs.tikxml.processor)
    implementation(libs.tikxml.annotation)
    implementation(libs.tikxml.retrofit)
}
