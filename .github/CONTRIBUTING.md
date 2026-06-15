# Contribution Guidelines

Contributions to **Shogun's DevPack** are always welcome. Did you find a bug or want to improve this project? Issues and/or Pull Requests are always welcome.

For bigger changes, please open an issue or message me on Discord (**almightyshogun**) first to discuss the details.

The following sections will explain the process in more detail and provide some guidelines on what contributions should look like.

# 🧹 Coding Style

This project follows the coding styles guidelines from [Official Kotlin Docs](https://kotlinlang.org/docs/coding-conventions.html#source-code-organization)

# 🪵 Git

All commit messages, branches, issues and/or pull requests will be in English.

### Branches

- **Main** — This is the `main` branch. This contains the latest stable release and is the exact source running in production.
- **Development** — This is the `development` branch. This contains the latest staging release that is marked for deployment and is the exact source running on staging.
- **Feature** — This is a `feature/*` branch. This contains a new feature that will be added. Any feature should have its own branch. Once completed the branch should be merged into the `development` branch.
- **Bugfix** — This is a `bugfix/*` branch. This contains a bugfix that will be added. Any bugfix should have its own branch. Once completed the branch should be merged into the `development` branch.

### Commit messages

Commit messages are bound to the following template:

- `<type>: <message>`
- `<type>(feature): <message>`
- `<type>(feature): <message> [<issue-number>]`
