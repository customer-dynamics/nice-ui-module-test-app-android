import java.util.Properties

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
} else {
    logger.warn("Unable to read local.properties")
}

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
        maven {
            name = "github-nice-devone-cxone-mobile"
            url = uri("https://maven.pkg.github.com/nice-devone/nice-cxone-mobile-sdk-android")
            credentials {
                username = (localProperties.getProperty("github.user")
                    ?: System.getenv("GPR_USERNAME")).orEmpty()
                password = (localProperties.getProperty("github.key")
                    ?: System.getenv("GPR_TOKEN")).orEmpty()
            }
        }
    }
}

rootProject.name = "SDK Test"
include(":app")
