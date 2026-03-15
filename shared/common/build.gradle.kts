plugins {
    id("lifemash.kmp.compose")
}

android {
    namespace = "org.bmsk.lifemash.feature.shared.common"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(project(":shared:designsystem"))
            implementation(project(":model"))
        }
    }
}
