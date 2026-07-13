---
name: release
description: Create a stable GitHub release for the Almighty-Shogun/shogun-devpack JetBrains plugin. Use when the user asks to cut, create, publish, or prepare a release. Resolves major, minor, patch, or explicit semver versions, runs safeguards, replaces Unreleased changelog and KDoc markers after approval, commits and pushes release metadata when needed, runs Gradle checks, generates release notes, requires explicit confirmation, then creates the GitHub release that triggers CI publishing. Never manually publishes to JetBrains Marketplace, pushes tags, or edits plugin version files.
---

# Release

Create a stable GitHub release for `Almighty-Shogun/shogun-devpack`.

Publishing is CI-driven:

- `.github/workflows/release.yml` runs on GitHub release publication.
- CI builds the plugin with `./gradlew buildPlugin -PpluginVersion=<tag>`.
- CI uploads `build/distributions/*` to the GitHub release.
- CI publishes the plugin to JetBrains Marketplace with repository secrets.

Do not run `publishPlugin`, push release tags manually, or edit local version files for a release. The Gradle build reads `pluginVersion` from `-PpluginVersion`; the repository default is intentionally `0.0.0`.

The current workflow supports stable releases only. Do not create beta or pre-release releases unless `.github/workflows/release.yml` is updated first.

## Resolve Version

Accept:

- `major`
- `minor`
- `patch`
- an explicit stable semver version, such as `1.2.0`

Refresh tags:

```bash
git fetch origin --tags --prune
```

For `major`, `minor`, or `patch`, compute from the highest stable semver tag:

```bash
git tag -l | grep -E '^[0-9]+\.[0-9]+\.[0-9]+$' | sort -V | tail -1
```

Bump:

- `major`: `X+1.0.0`
- `minor`: `X.Y+1.0`
- `patch`: `X.Y.Z+1`

For an explicit version, validate:

```text
^[0-9]+\.[0-9]+\.[0-9]+$
```

Show the resolved version before continuing. Tags should be plain semver, for example `1.2.0`, not `v1.2.0`, because CI passes the tag directly to `-PpluginVersion`.

## Safeguards

Run:

```bash
git rev-parse -q --verify "refs/tags/<version>"
gh release view "<version>"
git status --porcelain
git rev-parse origin/main
gh repo view --json nameWithOwner -q .nameWithOwner
```

Rules:

- If the tag or GitHub release already exists, stop.
- If the working tree is dirty, warn that local changes are not included in a release cut from `origin/main`. Continue only if the user accepts that.
- Capture the `origin/main` SHA. The release must target that SHA.
- Confirm the repository slug is `Almighty-Shogun/shogun-devpack` unless the user is intentionally releasing a fork.

## Prepare Release Metadata

Before running the release build checks, replace pending release markers when they exist.

Inspect:

```bash
rg -n "@since Unreleased" src/main/kotlin
rg -n "^## \\[Unreleased\\]" CHANGELOG.md
```

Rules:

- New unreleased KDocs use `@since Unreleased` during normal development.
- `CHANGELOG.md` uses `## [Unreleased]` during normal development.
- For a release, replace exact `@since Unreleased` occurrences with `@since <version>`.
- Replace the first exact `## [Unreleased]` heading in `CHANGELOG.md` with `## [<version>]`.
- Do not modify existing released `@since` tags.
- Do not edit Gradle version files or create local plugin version commits.

After applying the replacements, show:

```bash
git diff --stat
git diff -- CHANGELOG.md src/main/kotlin
```

Ask:

```text
Do you approve committing and pushing these release metadata changes?
```

If the user does not approve, stop before build checks and before release creation.

After approval, verify and commit only the metadata files that changed:

```bash
git diff --check
./gradlew compileKotlin
git add -- CHANGELOG.md <changed-kotlin-files>
git diff --cached --name-status
git commit -m "chore: prepare release metadata for <version>"
git push
git fetch origin --tags --prune
git rev-parse origin/main
```

Capture the new `origin/main` SHA after the push. The release must target this updated SHA.

## Build Check

Run the local checks that cover the release build surface:

```bash
./gradlew buildPlugin verifyPluginProjectConfiguration verifyPluginStructure
```

If any check fails, stop and show the failing command/output. Do not create a release.

## Release Notes

Generate release notes using the `release-notes` skill workflow:

- Base: latest published GitHub release tag unless the user provides a base.
- Head: `origin/main`.
- Prefer `CHANGELOG.md` entries for the resolved version or `[Unreleased]` when they exist.
- Compare link should end with `<base>...<version>` in the release body because the tag will exist after release creation.

Write the raw markdown body to a temporary file, such as:

```text
/tmp/shogun-devpack-release-<version>.md
```

Show the generated notes to the user.

## Review Gate

Before creating the release, state:

- version
- release title, which must exactly match the version
- stable release
- target SHA
- comparison base
- Gradle checks that passed

Show the exact `gh release create` command that will be run, then ask:

```text
Do you want me to create the release <version>?
```

Do not run `gh release create` until the user clearly approves.

## Create Release

After approval:

```bash
gh release create "<version>" \
    --target "<origin-main-sha>" \
    --title "<version>" \
    --notes-file "<notes-file>" \
    --latest
```

After creating the release, report:

- Release URL from `gh release view <version> --json url -q .url`.
- CI is now building the plugin ZIP and publishing to JetBrains Marketplace.
- No local version commits, manual tags, or manual publish commands were run.
