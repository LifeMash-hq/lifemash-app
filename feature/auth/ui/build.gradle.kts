plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.auth.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:auth:api"))
            implementation(project(":feature:auth:domain"))
            implementation(project(":shared:designsystem"))
        }
        androidMain.dependencies {
            implementation(libs.kakao.user)
            implementation(libs.google.identity.googleid)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
        }
    }
}
