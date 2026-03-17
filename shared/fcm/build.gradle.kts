plugins {
    id("lifemash.kmp.library")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.fcm"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.gitlive.firebase.messaging)
        }
        // iosMain: GitLive Firebase SDK (iOS 타겟 추가 시 활성화)
    }
}
