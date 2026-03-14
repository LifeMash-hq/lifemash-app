pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
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

include(
    ":domain:core",
    ":domain:feature-feed",
    ":domain:feature-history",
    ":domain:feature-scrap",
    ":domain:feature-search",
)

include(
    ":data:core",
    ":data:article",
    ":data:history",
    ":data:scrap",
    ":data:search",
)

include(
    ":core:common",
    ":core:designsystem",
    ":core:model",
    ":core:network",
)

include(
    ":feature:main-nav-graph",
    ":feature:main",
    ":feature:feed",
    ":feature:feed-api",
    ":feature:all",
    ":feature:scrap-api",
    ":feature:scrap",
    ":feature:webview-api",
    ":feature:webview",
)
