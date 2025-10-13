pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MiniShop"
include(
    ":app",
    ":core:common",
    ":core:ui",
    ":core:network",
    ":core:database",
    ":domain",
    ":data:product",
    ":data:impl",
    ":feature:home",
    ":feature:favorites",
    ":feature:cart",
    ":feature:profile"
)
