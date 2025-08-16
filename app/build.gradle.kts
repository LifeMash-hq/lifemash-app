import org.bmsk.buildsrc.Configuration

plugins {
    id("lifemash.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
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
            isMinifyEnabled = true
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
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.feature.main)
    implementation(projects.feature.mainNavGraph)
    implementation(projects.feature.scrap)
    implementation(projects.feature.topic)
    implementation(projects.feature.webview)
    implementation(projects.feature.feed)

    implementation(projects.core.repo.scrap.impl)
    implementation(projects.core.repo.search.impl)
    implementation(projects.core.repo.article)

    implementation(libs.androidx.appcompat)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}
