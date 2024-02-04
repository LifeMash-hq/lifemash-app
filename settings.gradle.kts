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

    ":core:model",
    ":core:domain",
    ":core:network",
    ":core:data",
    ":core:common-ui",

    ":feature:topic",
)
