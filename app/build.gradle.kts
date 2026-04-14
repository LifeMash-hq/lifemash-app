import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("lifemash.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val localProps = gradleLocalProperties(rootDir, providers)
val kakaoNativeAppKey: String = localProps.getProperty("KAKAO_NATIVE_APP_KEY", "")
val prodBackendBaseUrl = "https://lifemash.app"
val devBackendBaseUrl = "https://dev.lifemash.app"
val debugBackendUrl: String = localProps.getProperty("BACKEND_BASE_URL", null)
    ?: devBackendBaseUrl

android {
    defaultConfig {
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoNativeAppKey
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoNativeAppKey\"")
    }
    signingConfigs {
        create("release") {
            storeFile = rootProject.file(localProps.getProperty("RELEASE_STORE_FILE", ""))
            storePassword = localProps.getProperty("RELEASE_STORE_PASSWORD", "")
            keyAlias = localProps.getProperty("RELEASE_KEY_ALIAS", "")
            keyPassword = localProps.getProperty("RELEASE_KEY_PASSWORD", "")
        }
    }
    buildTypes {
        debug {
            buildConfigField("String", "BACKEND_BASE_URL", "\"$debugBackendUrl\"")
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("String", "BACKEND_BASE_URL", "\"$prodBackendBaseUrl\"")
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.feature.main)
    implementation(projects.shared.platform)
    implementation(projects.domain)
    implementation(projects.shared.fcm)
    implementation(libs.koin.android)
    implementation(libs.kakao.user)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.core.ktx)
    implementation(libs.androidx.datastore.preferences)
}