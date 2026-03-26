plugins {
    id("lifemash.kmp.library")
    `maven-publish`
}

group = "org.bmsk.lifemash"
version = "0.1.0"

kotlin {
    android {
        namespace = "org.bmsk.lifemash.validation"
    }

    jvm()

    sourceSets {
        commonMain.dependencies { }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/LifeMash-hq/lifemash-app")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: ""
                password = System.getenv("GITHUB_PACKAGES_TOKEN") ?: System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}
