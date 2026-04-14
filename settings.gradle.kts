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
    ":feature:auth:api",
    ":feature:auth:impl",
)

include(
    ":feature:calendar:api",
    ":feature:calendar:impl",
)

include(
    ":feature:profile:api",
    ":feature:profile:impl",
)

include(
    ":feature:event-detail:api",
    ":feature:event-detail:impl",
)

include(
    ":feature:feed:api",
    ":feature:feed:impl",
)

include(
    ":feature:memo:api",
    ":feature:memo:impl",
)

include(
    ":feature:moment:api",
    ":feature:moment:impl",
)

include(
    ":feature:onboarding:api",
    ":feature:onboarding:impl",
)

include(
    ":feature:notification:api",
    ":feature:notification:impl",
)

include(":domain")

include(
    ":data:core",
    ":data:remote",
    ":data:local",
)

include(
    ":shared:designsystem",
    ":shared:platform",
    ":shared:fcm",
)
