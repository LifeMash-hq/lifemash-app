import org.bmsk.buildsrc.Configuration

plugins {
    id("lifemash.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "org.bmsk.lifemash"
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
                "proguard-rules.pro",
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
        dataBinding = true
    }
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.commonUi)

    implementation(projects.feature.main)
    implementation(projects.feature.mainNavGraph)
    implementation(projects.feature.scrap)
    implementation(projects.feature.topic)
    implementation(projects.feature.webview)

    implementation(libs.androidx.appcompat)
}
