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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
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
include(":common:utils")
include(":core")
include(":core:domain")
include(":core:model")
include(":core:data")
include(":core:auth")
include(":core:auth:data")
include(":core:auth:domain")
include(":feature")
include(":feature:registration")
include(":core:constant")
include(":core:navigation")
include(":feature:login")
include(":feature:forgotpassword")
include(":feature:termconditions")
include(":feature:userprofile")
include(":feature:admin")
include(":feature:admin:admin-main")
include(":feature:admin:domain")
include(":feature:admin:add-topic")
include(":feature:admin:add-master-tutor")
include(":feature:admin:master-home")
include(":feature:admin:common")
include(":feature:student")
include(":feature:tutor")
include(":feature:student:home")
include(":core:meet")
include(":feature:student:domain")
include(":feature:student:slot-booking")
include(":feature:tutor:home")
include(":feature:tutor:edit-tutor")
include(":feature:admin:tutor-detail-verification")
include(":feature:admin:add-tutor")
include(":core:auth:google-oauth")
include(":core:datastore")
