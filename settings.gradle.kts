import org.jetbrains.intellij.platform.gradle.extensions.intellijPlatform

rootProject.name = "shogun-devpack"

pluginManagement {
    plugins {
        id("org.jetbrains.changelog") version "2.5.0"
        id("org.jetbrains.kotlin.jvm") version "2.2.20"
    }
}

plugins {
    id("org.jetbrains.intellij.platform.settings") version "2.16.0"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()

        intellijPlatform {
            defaultRepositories()
        }
    }
}
