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

rootProject.name = "LifeMash_NewsApp"
include(
    ":app",

    ":core:designsystem",
    ":core:model",
    ":core:domain",
    ":core:network",
    ":core:data",
    ":core:common-ui",

    ":core:repo:scrap:api",
    ":core:repo:scrap:impl",

    ":feature:main-nav-graph",

    ":feature:main",
    ":feature:topic",
    ":feature:all",

    ":feature:scrap-api",
    ":feature:scrap",
)
