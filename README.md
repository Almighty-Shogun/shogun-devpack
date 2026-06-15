<a href="https://shogun.ms" target="_blank" rel="noopener">
	<img src="https://cdn.shogun.ms/assets/branding/app-icon-256.svg" alt="Shogun app-icon" height="62"/>
</a>

---

# Shogun's DevPack

<!-- Plugin description -->
Shogun's DevPack is a personal productivity plugin for JetBrains IDEs. It bundles a dark IDE theme, editor utilities, project/file helpers, code sharing tools, and AI terminal integrations aimed at reducing repetitive project setup work.

Core features:

- A dark JetBrains theme and matching editor color scheme.
- File creation actions for C#, Vue, JSON, Markdown, Git helper files, and general project files.
- C# namespace detection when creating C# files.
- Vue component bundle and store/composable templates with bundle index export handling.
- Git helper files, including CODEOWNERS, LICENSE, .gitignore, .gitkeep, and .editorconfig templates.
- License generation for common GitHub license templates.
- Code Share uploads for selected code or selected files through GitHub Gist, Pastebin, or a custom server.
- Code Shot screenshot creation from the current editor selection, with clipboard, Freeimage.host, ImgBB, and custom server outputs.
- Shift-tab navigation actions for moving the caret between aligned positions.
- Optional Project View cleanup that hides the root project path.
- Optional Codex and Claude tool windows that open project-root terminals and can resume or continue the latest project session.
<!-- Plugin description end -->

## ⚠️ Requirements

- JetBrains IDE 2026.1.2 or newer.
- Java 21 for local builds.
- The bundled JetBrains Terminal plugin must be available.
- Codex CLI is optional and only required when using the Codex tool window.
- Claude Code CLI is optional and only required when using the Claude tool window.

## 🛠️ Building

```sh
# Clone the repository
git clone https://github.com/Almighty-Shogun/shogun-devpack.git

./gradlew buildPlugin # Build the plugin locally
./gradlew runIde # Run the plugin in a sandbox IDE
.gradlew verifyPluginProjectConfiguration verifyPluginStructure # Run the main verification
```

## 📦 Semantic Versioning (SemVer)

This project follows Semantic Versioning (SemVer), which uses a version format of `MAJOR.MINOR.PATCH`.

- **Patch** — Increases when backward-compatible bug fixes are made. These are small changes that address incorrect behavior without adding new features or functionality.
- **Minor** — Increases when new features or functionality are added in a backward-compatible manner. This includes adding new methods, classes, or capabilities that do not break existing code.
- **Major** — Increases when breaking changes are introduced that are not backward-compatible, such as modifying method signatures, removing functionality, or changing existing behavior in a way that may break dependent code.
