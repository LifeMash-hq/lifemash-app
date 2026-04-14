pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
    }
}

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:testClasses"))

rootProject.name = "LifeMash"
include(":app")
include(":lint")

// Feature
include(
    ":feature:main",
    ":feature:auth:api",
    ":feature:auth:impl",
    ":feature:calendar:api",
    ":feature:calendar:impl",
    ":feature:event-detail:api",
    ":feature:event-detail:impl",
    ":feature:feed:api",
    ":feature:feed:impl",
    ":feature:memo:api",
    ":feature:memo:impl",
    ":feature:moment:api",
    ":feature:moment:impl",
    ":feature:notification:api",
    ":feature:notification:impl",
    ":feature:onboarding:api",
    ":feature:onboarding:impl",
    ":feature:profile:api",
    ":feature:profile:impl",
)

// Domain
include(":domain")

// Data
include(
    ":data:core",
    ":data:remote",
    ":data:local",
)

// Shared
include(
    ":shared:designsystem",
    ":shared:platform",
    ":shared:fcm",
)
