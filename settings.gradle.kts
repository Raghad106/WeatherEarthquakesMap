pluginManagement {
    repositories {
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://maven.google.com/") }
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
    }
}

rootProject.name = "Weather & Earthquake Map"
include(":app")
 