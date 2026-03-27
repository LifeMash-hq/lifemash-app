import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("lifemash.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val localProps = gradleLocalProperties(rootDir, providers)

val kakaoKeyDebug = localProps.getProperty("KAKAO_NATIVE_APP_KEY_DEBUG", "")
val kakaoKeyRelease = localProps.getProperty("KAKAO_NATIVE_APP_KEY_RELEASE", "")
val debugBackendUrl = localProps.getProperty("BACKEND_BASE_URL_DEBUG", "http://10.0.2.2:8080")
val releaseBackendUrl = localProps.getProperty("BACKEND_BASE_URL_RELEASE", "https://lifemash-backend.onrender.com")

android {
    defaultConfig { }
    signingConfigs {
        create("release") {
            val storeFilePath = localProps.getProperty("RELEASE_STORE_FILE", "")
            if (storeFilePath.isNotBlank()) {
                storeFile = rootProject.file(storeFilePath)
            }
            storePassword = localProps.getProperty("RELEASE_STORE_PASSWORD", "")
            keyAlias = localProps.getProperty("RELEASE_KEY_ALIAS", "")
            keyPassword = localProps.getProperty("RELEASE_KEY_PASSWORD", "")
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKeyDebug
            buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKeyDebug\"")
            buildConfigField("String", "BACKEND_BASE_URL", "\"$debugBackendUrl\"")
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoKeyRelease
            buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKeyRelease\"")
            buildConfigField("String", "BACKEND_BASE_URL", "\"$releaseBackendUrl\"")
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.feature.main)
    implementation(projects.shared.common)
    implementation(projects.shared.fcm)
    implementation(projects.feature.notification.domain)
    implementation(projects.feature.notification.data)

    implementation(libs.koin.android)
    implementation(libs.androidx.room.runtime)
    implementation(libs.kakao.user)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.core.ktx)
    implementation(libs.androidx.datastore.preferences)
}