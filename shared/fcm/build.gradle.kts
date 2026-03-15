import com.android.build.api.dsl.LibraryExtension
plugins {
    id("lifemash.kmp.library")
}

configure<com.android.build.api.dsl.LibraryExtension> {
    namespace = "org.bmsk.lifemash.fcm"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.gitlive.firebase.messaging)
        }
        // iosMain: GitLive Firebase SDK (iOS 타겟 추가 시 활성화)
    }
}
