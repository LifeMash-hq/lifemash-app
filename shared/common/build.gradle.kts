plugins {
    id("lifemash.kmp.compose")
}

kotlin {
    android {
        namespace = "org.bmsk.lifemash.feature.shared.common"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.ui)
            implementation(project(":shared:designsystem"))
            implementation(project(":shared:model"))
        }
    }
}
