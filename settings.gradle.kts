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
    }
}

gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:testClasses"))

rootProject.name = "LifeMash"
include(":app")
include(":model")
include(":lint")

include(
    ":feature:feed:domain",
    ":feature:feed:data",
    ":feature:feed:api",
    ":feature:feed:ui",
)

include(
    ":feature:scrap:domain",
    ":feature:scrap:data",
    ":feature:scrap:api",
    ":feature:scrap:ui",
)

include(
    ":feature:history:api",
    ":feature:history:ui",
)

include(
    ":feature:main",
)

include(
    ":feature:calendar:domain",
    ":feature:calendar:data",
)

include(
    ":feature:notification:domain",
    ":feature:notification:data",
)

include(
    ":feature:auth:domain",
    ":feature:auth:data",
)

include(
    ":shared:network",
    ":shared:navigation",
    ":shared:designsystem",
    ":shared:common",
    ":shared:webview",
    ":shared:fcm",
)
