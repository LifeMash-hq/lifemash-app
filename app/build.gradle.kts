import org.bmsk.buildsrc.Configuration

plugins {
    id("lifemash.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    namespace = "org.bmsk.lifemash"
    compileSdk = Configuration.compileSdk

    defaultConfig {
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
    coreLibraryDesugaring(libs.android.desugarJdkLibs)

    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.commonUi)
    implementation(projects.presentation)

    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintLayout)
    implementation(libs.androidx.databinding)
    implementation(libs.androidx.appcompat)

    // glide
    implementation(libs.glide)

    // di
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)
}
