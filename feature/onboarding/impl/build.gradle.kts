plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.onboarding.impl"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":domain"))
            implementation(project(":feature:onboarding:api"))
            implementation(project(":shared:designsystem"))
        }
    }
}
