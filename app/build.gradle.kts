import org.bmsk.buildsrc.Configuration

plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.hilt.plugin.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

android {
    namespace = Configuration.namespace
    compileSdk = Configuration.compileSdk

    defaultConfig {
        applicationId = Configuration.applicationId
        minSdk = Configuration.minSdk
        targetSdk = Configuration.targetSdk
        versionCode = Configuration.versionCode
        versionName = Configuration.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // TikXML
    // 가장 최신 버전이 이슈가 많아서 8.13 버전을 사용함
    implementation(libs.tikxml.core)
    implementation(libs.tikxml.annotation)
    kapt(libs.tikxml.processor)
    implementation(libs.tikxml.retrofit)

    // jsoup
    implementation(libs.jsoup)

    // glide
    implementation(libs.glide)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // lottie
    implementation(libs.lottie)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}