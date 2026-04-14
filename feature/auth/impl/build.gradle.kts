plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.auth.impl"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":feature:auth:api"))
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
