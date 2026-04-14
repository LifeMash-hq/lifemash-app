plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.shared.platform"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:designsystem"))
        }
    }
}
