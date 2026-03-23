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
    ":feature:assistant:domain",
    ":feature:assistant:data",
    ":feature:assistant:api",
    ":feature:assistant:ui",
)

include(
    ":feature:home:api",
    ":feature:home:domain",
    ":feature:home:data",
    ":feature:home:ui",
)

include(
    ":shared:model",
    ":shared:network",
    ":shared:navigation",
    ":shared:designsystem",
    ":shared:common",
    ":shared:webview",
    ":shared:fcm",
)
