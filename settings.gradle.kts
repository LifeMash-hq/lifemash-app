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

gradle.startParameter.excludedTaskNames.addAll(listOf(":buildSrc:testClasses"))
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:testClasses"))

rootProject.name = "LifeMash"
include(
    ":app",

    ":domain:core",
    ":domain:feature-feed",
    ":domain:feature-scrap",

    ":domain:feature-search",

    ":data:core",
    ":data:article",
    ":data:scrap",

    ":data:search",

    ":core:designsystem",
    ":core:model",
    ":core:network",
    ":core:common-ui",

    

    ":feature:main-nav-graph",

    ":feature:main",
    ":feature:feed",
    ":feature:feed-api",

    ":feature:all",

    ":feature:scrap-api",
    ":feature:scrap",

    ":feature:webview-api",
    ":feature:webview",

    //    ":feature:topic",
    //    ":feature:topic-api",
)
