plugins {
    id("lifemash.kmp.library")
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
}

group = "org.bmsk.lifemash"
version = "0.1.0"

kotlin {
    android {
        namespace = "org.bmsk.lifemash.model"
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/LifeMash-hq/LifeMash-App")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: ""
                password = System.getenv("GITHUB_PACKAGES_TOKEN") ?: System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}
