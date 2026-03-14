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
    ":feature:main",
)

include(
    ":shared:network",
    ":shared:navigation",
    ":shared:designsystem",
    ":shared:common",
    ":shared:webview",
)
