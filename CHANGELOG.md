# Shogun's DevPack Changelog

## [Unreleased]

### Fixed

- Fixed Codex and Claude terminal sessions so multiple JetBrains project windows keep independent active terminal state.
- Fixed Escape forwarding in Codex and Claude terminals so interrupting a prompt no longer writes directly to the terminal connector from the key event dispatcher.

## [1.2.1]

### Fixed

- Fixed Escape in Codex and Claude terminals so it is sent to the active prompt even when an editor file is open.

## [1.2.0]

### Changed

- Changed Code Shot to render screenshots from the active editor instead of creating a detached editor.
- Changed Code Shot to rely on the bundled color scheme for suppressing matching tag, matched brace, and identifier-under-caret highlights.

### Fixed

- Fixed Code Shot screenshots so only the selected editor lines are rendered.
- Fixed Code Shot screenshots so syntax colors are preserved in TypeScript, HTML, Vue, and other highlighted files.
- Fixed Code Shot screenshots so HTML/XML matching tag highlight backgrounds such as `<template>` are no longer captured.
- Fixed Code Shot screenshots clipping after-line-end inlay text such as usage hints.

## [1.1.1]

### Fixed

- Fixed Code Shot rendering by using a detached editor so live editor selections, caret highlights, and HTML/XML tag highlights no longer leak into screenshots.
- Fixed Code Shot screenshots clipping the trailing characters of long selected lines.

## [1.1.0]

### Added

- Added line sorting for selected editor lines by length.
- Added optional Project View auto-hide behavior when opening AI terminals or moving files with Shift Tab.

### Fixed

- Fixed Code Shot rendering so transient editor highlights are not included in generated screenshots.
- Fixed Code Shot editor state restoration after rendering.

## [1.0.0]

### Added

- Added the Shogun's DevPack plugin identity, branding, description, and release metadata.
- Added a bundled dark theme and matching editor color scheme based on Island Dark.
- Added file creation actions for C#, Vue, JSON, Markdown, Git helper files, and .editorconfig.
- Added C# templates for services, JSON models, remote commands, console commands, and dependency injection pairs.
- Added Vue templates for components, component bundles, composables, and Pinia stores.
- Added Vue bundle index export handling and duplicate export cleanup.
- Added Git helper file actions for CODEOWNERS, LICENSE, .gitignore, and .gitkeep.
- Added GitHub-style license generation with selectable license templates and required placeholder fields.
- Added Code Share uploads for editor selections and selected files through GitHub Gist, Pastebin, and custom server targets.
- Added code screenshot creation from the current editor selection with clipboard, Freeimage.host, ImgBB, and custom server outputs.
- Added Shift Tab actions for moving the caret between aligned editor positions.
- Added optional Project View root-path hiding.
- Added optional Codex and Claude tool window integrations with project-root startup, session resume or continue support, custom executable paths, and per-project arguments.
- Added plugin settings for Project View cleanup, Code Share providers, Code Shot outputs, and AI terminal behavior.
- Added GitHub release workflow for building and attaching the plugin ZIP.
