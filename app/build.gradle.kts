import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.net.NetworkInterface
import java.net.Inet4Address

plugins {
    id("lifemash.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

fun getLocalIpAddress(): String {
    return try {
        NetworkInterface.getNetworkInterfaces().asSequence()
            .flatMap { it.inetAddresses.asSequence() }
            .firstOrNull { !it.isLoopbackAddress && it is Inet4Address && !it.hostAddress.startsWith("172.") }
            ?.hostAddress ?: "10.0.2.2"
    } catch (e: Exception) {
        "10.0.2.2"
    }
}

val localProps = gradleLocalProperties(rootDir, providers)
val kakaoNativeAppKey: String = localProps.getProperty("KAKAO_NATIVE_APP_KEY", "")
val debugBackendUrl: String = localProps.getProperty("BACKEND_BASE_URL", null)
    ?: "http://${getLocalIpAddress()}:8080"
println("💡 Resolved Debug Backend URL: $debugBackendUrl")

val releaseBackendUrl = "https://lifemash-backend.onrender.com"

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