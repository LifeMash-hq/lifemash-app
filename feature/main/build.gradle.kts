plugins {
    id("lifemash.kmp.compose")
}

android {
    namespace = "org.bmsk.lifemash.main"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared:common"))
            implementation(project(":shared:webview"))
            implementation(project(":shared:designsystem"))
            implementation(project(":model"))
            implementation(project(":feature:feed:api"))
            implementation(project(":feature:feed:ui"))
            implementation(project(":feature:scrap:api"))
            implementation(project(":feature:scrap:ui"))
            implementation(project(":feature:history:api"))
            implementation(project(":feature:history:ui"))
            implementation(project(":feature:notification:api"))
            implementation(project(":feature:notification:ui"))
            implementation(project(":feature:calendar:api"))
            implementation(project(":feature:calendar:ui"))
            implementation(project(":feature:auth:api"))
            implementation(project(":feature:auth:ui"))
            implementation(project(":feature:auth:domain"))

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)

            implementation(libs.jetbrains.navigation.compose)
            implementation(libs.koin.compose)
        }
        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
        }
        iosMain.dependencies {
            implementation(project(":shared:network"))
            implementation(project(":feature:feed:domain"))
            implementation(project(":feature:feed:data"))
            implementation(project(":feature:scrap:domain"))
            implementation(project(":feature:scrap:data"))
            implementation(project(":feature:calendar:domain"))
            implementation(project(":feature:calendar:data"))
            implementation(project(":feature:auth:domain"))
            implementation(project(":feature:auth:data"))
            implementation(project(":feature:notification:domain"))
            implementation(project(":feature:notification:data"))
            implementation(libs.koin.core)
            implementation(libs.ktor.client.darwin)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.androidx.datastore.preferences)
        }
    }
}
