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

rootProject.name = "LifeMash"
include(
    ":app",

    ":core:designsystem",
    ":core:model",
    ":core:network",
    ":core:common-ui",

    ":core:repo:scrap:api",
    ":core:repo:scrap:impl",
    ":core:repo:search:api",
    ":core:repo:search:impl",

    ":feature:main-nav-graph",

    ":feature:main",
    ":feature:topic",
    ":feature:topic-api",

    ":feature:all",

    ":feature:scrap-api",
    ":feature:scrap",

    ":feature:webview-api",
    ":feature:webview",
)
