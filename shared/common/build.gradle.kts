plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.feature.shared.common"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:designsystem"))
            implementation(project(":shared:model"))
        }
    }
}
