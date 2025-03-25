pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // 프로젝트에서 리포지토리 추가 불가
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "number"
include(":app")