# Shogun's DevPack Changelog

## [Unreleased]

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
