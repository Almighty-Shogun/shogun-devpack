import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode

plugins {
    id("org.jetbrains.changelog")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
}

val pluginVersionProvider = providers.gradleProperty("pluginVersion").orElse("0.0.0")

group = "ms.shogun"
version = pluginVersionProvider.get()

changelog {
    groups = listOf("Added", "Changed", "Removed", "Fixed")
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        jvmDefault = JvmDefaultMode.NO_COMPATIBILITY
    }
}

dependencies {
    intellijPlatform {
        bundledPlugin("org.jetbrains.plugins.terminal")
        intellijIdea(providers.gradleProperty("platformVersion"))
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "Shogun's DevPack"
        version = pluginVersionProvider

        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog

        changeNotes = pluginVersionProvider.map { pluginVersion ->
            with(changelog) {
                val changelogItem = getOrNull(pluginVersion)
                    ?: runCatching { getUnreleased() }.getOrElse { getLatest() }

                renderItem(changelogItem, Changelog.OutputType.HTML)
            }
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
        }
    }

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
}

tasks {
    processResources {
        exclude("fileTemplates/internal/**")
        from(fileTree("src/main/resources/fileTemplates/internal").files) {
            eachFile {
                relativePath = RelativePath(true, "fileTemplates", "internal", this.name)
            }
        }
    }
}
