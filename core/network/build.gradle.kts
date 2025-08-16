plugins {
    id("lifemash.android.library")
    id("lifemash.android.hilt")
    id("kotlinx-serialization")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "org.bmsk.lifemash.core.network"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }
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

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.coroutines.test)
}
