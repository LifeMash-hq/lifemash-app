plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
}

group = "org.bmsk.lifemash"
version = "0.1.0"

dependencies {
    implementation(project(":shared:model"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "org.bmsk.lifemash"
            artifactId = "backend-api"
        }
    }
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
