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
        iosMain.dependencies {
            implementation(libs.gitlive.firebase.messaging)
        }
    }
}
