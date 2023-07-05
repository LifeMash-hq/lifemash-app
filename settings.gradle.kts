pluginManagement {
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
include(":app")
include(":core:model")
include(":core:domain")
include(":core:network")
include(":core:data")
include(":core:common-ui")
include(":core:database")
include(":core:interactor")
include(":core:presentation")
include(":core:chatgpt")
