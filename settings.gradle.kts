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
include(":model")

include(
    ":domain:feature-feed",
    ":domain:feature-history",
    ":domain:feature-scrap",
    ":domain:feature-search",
)

include(
    ":data:article",
    ":data:history",
    ":data:network",
    ":data:scrap",
    ":data:search",
)

include(
    ":feature-shared:designsystem",
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
