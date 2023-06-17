plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

android {
    namespace = "org.bmsk.network"
    compileSdk = org.bmsk.buildsrc.Configuration.compileSdk

    defaultConfig {
        minSdk = org.bmsk.buildsrc.Configuration.minSdk
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
}