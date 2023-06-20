import org.bmsk.buildsrc.Configuration

plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

android {
    namespace = "org.bmsk.data"
    compileSdk = Configuration.compileSdk

    defaultConfig {
        minSdk = Configuration.minSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:model"))

    // coroutines
    implementation(libs.coroutines)

    // di
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)

    // retrofit
    implementation(libs.retrofit)

    // jsoup
    implementation(libs.jsoup)

    // test
    testImplementation(libs.junit)
    testImplementation(libs.mockito)
}