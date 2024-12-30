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

rootProject.name = "Buddha Tutor"
include(":app")
include(":admin")
include(":common")
include(":common:navigation")
include(":common:utils")
include(":auth")
include(":common:domain")
include(":common:data")
include(":user-profile")
include(":user")
