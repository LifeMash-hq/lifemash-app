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

include(
    ":feature:main",
)

include(
    ":feature:calendar:domain",
    ":feature:calendar:data",
    ":feature:calendar:api",
    ":feature:calendar:ui",
)

include(
    ":feature:notification:domain",
    ":feature:notification:data",
    ":feature:notification:api",
    ":feature:notification:ui",
)

include(
    ":feature:auth:domain",
    ":feature:auth:data",
    ":feature:auth:api",
    ":feature:auth:ui",
)

include(
    ":feature:profile:api",
    ":feature:profile:domain",
    ":feature:profile:data",
    ":feature:profile:ui",
)

include(
    ":feature:event-detail:api",
    ":feature:event-detail:domain",
    ":feature:event-detail:data",
    ":feature:event-detail:ui",
)

include(
    ":feature:feed:api",
    ":feature:feed:domain",
    ":feature:feed:data",
    ":feature:feed:ui",
)

include(
    ":shared:model",
    ":shared:network",
    ":shared:navigation",
    ":shared:designsystem",
    ":shared:common",
    ":shared:fcm",
    ":shared:validation",
)

// Core (Backend)
include(
    ":core:domain",
    ":core:infra",
    ":core:ktor",
)
