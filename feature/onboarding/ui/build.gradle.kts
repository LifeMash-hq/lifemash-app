plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.onboarding.ui"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":feature:onboarding:api"))
            implementation(project(":feature:onboarding:domain"))
            implementation(project(":shared:designsystem"))
        }
    }
}
