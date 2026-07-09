---
name: release-notes
description: Generate GitHub release notes or a changelog for the Almighty-Shogun/shogun-devpack JetBrains plugin by using CHANGELOG.md and diffing a base ref against origin/main. Use when the user asks for release notes, a changelog, or a summary of changes for an upcoming or specific plugin release. This skill is read-only and never creates tags, releases, commits, or publishes the plugin.
---

# Release Notes

Generate release notes for `Almighty-Shogun/shogun-devpack`.

This workflow is read-only. Do not create tags, releases, commits, upload artifacts, or publish the plugin.

## Resolve Range

Refresh tags when needed:

```bash
git fetch origin --tags --prune
```

Use:

- Head: `origin/main`.
- Default base: latest published GitHub release tag.
- Explicit base: the tag or ref provided by the user.

Useful commands:

```bash
gh release view --json tagName -q .tagName
git rev-parse "<base>"
git rev-parse origin/main
gh repo view --json nameWithOwner -q .nameWithOwner
```

If `<base>` and `origin/main` point to the same commit, report that there are no changes and stop.

## Gather Changes

Run:

```bash
git log --oneline --no-merges <base>..origin/main
git diff --name-status <base>..origin/main -- 'src/**' 'README.md' 'CHANGELOG.md' 'build.gradle.kts' 'gradle.properties' '.github/**'
git diff --stat <base>..origin/main
```

Read relevant diffs before describing behavior. Do not rely only on commit subjects when a change affects plugin actions, settings, upload behavior, templates, resources, workflows, or JetBrains compatibility.

## Prefer CHANGELOG Entries

Read `CHANGELOG.md` first.

- For a known release version, prefer the matching `## [<version>]` section.
- For an upcoming release, prefer `## [Unreleased]`.
- If the changelog section is empty or incomplete, derive missing details from the diff.
- Do not invent user-facing changes from internal cleanup alone.

## Classify

Group changes by plugin area first. Then classify entries inside each group.

Plugin area groups:

- `Code Shot`: screenshot rendering, clipboard image handling, hosted image uploads, and Code Shot settings.
- `Code Share`: GitHub Gist, Pastebin, custom server uploads, code extraction, and Code Share settings.
- `Editor tools`: line sorting and Shift Tab caret navigation.
- `File templates`: generated C#, Vue, JSON, Markdown, Git helper, license, and editorconfig files.
- `AI terminals`: Codex and Claude terminal integrations and related settings.
- `Project View`: project tree cleanup and auto-hide behavior.
- `Theme and resources`: bundled theme, color scheme, icons, messages, and plugin metadata.
- `Settings`: shared configurable UI and persisted settings that are not specific to another area.
- `Build and release`: Gradle, CI workflows, publishing, signing, verification, and release metadata.
- `Internal`: maintenance that is worth mentioning but not directly user-facing.

Use these subsection labels inside each group when they have content:

- Breaking changes: removed actions, changed settings semantics, changed upload contracts, changed supported IDE versions, or removed behavior.
- Added: new actions, settings, templates, integrations, upload targets, or resources.
- Changed: behavior updates that are not fixes.
- Fixed: corrected rendering, uploads, templates, settings persistence, UI behavior, build, publishing, or compatibility issues.
- Documentation: meaningful README or changelog changes.
- Internal: cleanup, refactors, dependency updates, or tooling updates. Omit these unless they materially affect users or maintainers.

## Render

Output one copy-paste-ready markdown block in English. Use plugin-area grouping:

```markdown
## Code Shot

### Fixed

- Fixed screenshot rendering so transient editor tag highlights are not captured while syntax colors are preserved.

## Build and release

### Changed

- Updated the release workflow to build the plugin ZIP from the GitHub release tag.

**Full Changelog:** https://github.com/Almighty-Shogun/shogun-devpack/compare/<base>...origin/main
```

Rules:

- Only include plugin-area groups that have content.
- Only include subsections with content.
- Keep entries concise and user-facing.
- Mention JetBrains Marketplace publishing only for workflow or release changes.
- Do not use em dashes or en dashes.
- Use complete sentences.
- Build the compare link from the resolved repo slug and refs.
- For release bodies created before the tag exists, use `<base>...<version>` instead of `<base>...origin/main` when instructed by the release workflow.
- After the markdown block, briefly state the resolved range and any important caveats.
