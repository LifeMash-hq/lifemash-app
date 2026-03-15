plugins {
    id("lifemash.kmp.compose")
}

android {
    namespace = "org.bmsk.lifemash.feature.designsystem"

    lint {
        disable += "DesignSystem"
    }
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.compose.ui.text.google.fonts)
            implementation(libs.androidx.compose.ui.tooling.preview)
        }
    }
}
