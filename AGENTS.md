# Repository Instructions

Use this file as the root guidance for the Shogun's DevPack plugin.

## Project

Shogun's DevPack is a JetBrains IDE plugin written in Kotlin. It provides editor tools, file templates, Code Share uploads, Code Shot screenshots, Project View behavior, a bundled theme/color scheme, and optional Codex/Claude terminal integrations.

The plugin is configured through Gradle with the JetBrains IntelliJ Platform Gradle plugin. The default local plugin version is `0.0.0`; release builds pass the real version with `-PpluginVersion=<semver>`.

## Repository Layout

- `src/main/kotlin/ms/shogun/devpack`: Kotlin plugin source.
- `src/main/resources/META-INF/plugin.xml`: plugin registrations, actions, and configurables.
- `src/main/resources/messages/MyMessageBundle.properties`: user-facing action/configuration text.
- `src/main/resources/fileTemplates/internal`: bundled file templates, remapped by `processResources`.
- `src/main/resources/themes` and `src/main/resources/colors`: bundled theme and editor color scheme.
- `.github/workflows/release.yml`: GitHub release triggered build, artifact upload, and JetBrains Marketplace publishing.
- `.agents/skills`: project-local agent skills for release and release notes workflows.

## Build And Verification

Use Java 21.

Common commands:

```bash
./gradlew compileKotlin
./gradlew buildPlugin
./gradlew runIde
./gradlew verifyPluginProjectConfiguration verifyPluginStructure
```

For release readiness, run:

```bash
./gradlew buildPlugin verifyPluginProjectConfiguration verifyPluginStructure
```

If Gradle needs to write under `~/.gradle` in a restricted sandbox, request approval instead of working around the cache.

## Development Notes

- Preserve existing Kotlin style and package layout.
- Keep edits scoped to the requested plugin area.
- Use `apply_patch` for manual edits.
- Do not use Codex skills unless the user specifically asks for a skill or explicitly asks for a workflow that requires one.
- Do not revert unrelated local changes. This repo may have user changes in progress.
- Prefer IntelliJ Platform stable APIs. Avoid deprecated or `@ApiStatus.Experimental` APIs unless the user explicitly accepts the tradeoff.
- New Kotlin KDoc `@since` tags for unreleased code must use `Unreleased`. Existing released `@since` tags should keep the version they shipped in. The release workflow replaces `@since Unreleased` with the resolved release version after user approval.
- User-facing strings should generally live in `MyMessageBundle.properties` when they are action/configuration labels.
- File template resources under `src/main/resources/fileTemplates/internal` are intentionally remapped by `processResources`; preserve that build behavior.

## Code Shot Caution

Code Shot rendering is sensitive because it must satisfy several constraints at once:

- Render only the selected editor range.
- Preserve language syntax and semantic colors across TypeScript, HTML, Vue, and other IDE-supported languages.
- Avoid capturing transient editor state such as selection colors, caret row, search results, brace highlights, and HTML/XML matching tag highlights.
- Restore editor state after rendering.

When changing `src/main/kotlin/ms/shogun/devpack/codeShot/CodeFragment.kt`, verify with:

```bash
./gradlew compileKotlin
```

Also ask the user to visually test at least one HTML/Vue selection and one TypeScript selection if local IDE automation is unavailable.

## Release

Releases are stable semver tags such as `1.1.1`, not `v1.1.1`.

Creating a GitHub release triggers `.github/workflows/release.yml`, which:

- Builds the plugin ZIP with `./gradlew buildPlugin -PpluginVersion=<tag>`.
- Uploads `build/distributions/*` to the GitHub release.
- Publishes to JetBrains Marketplace using repository secrets.

Do not manually run `publishPlugin`, push release tags, create local version commits, or edit version files for a release unless the user specifically changes the release process.

Do not use the project-local skills unless the user specifically asks for them.
