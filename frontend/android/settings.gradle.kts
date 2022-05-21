dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "elevated-android"

include(":app")

includeBuild("../../") {
    dependencySubstitution {
        substitute(module("dev.bnorm.elevated:common-model")).using(project(":common:model"))
    }
}
