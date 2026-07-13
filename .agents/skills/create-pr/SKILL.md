---
name: create-pr
description: Create a GitHub pull request for Almighty-Shogun/shogun-devpack from an optional branch name. Use when the user invokes $create-pr, optionally with a fix/* or feat/* branch parameter, asks to reuse the current fix/* or feat/* branch, infer a branch name for current changes, commit selected files through the user-level $commit skill, generate a PR title/body in the repository's personal style, and create the PR only after explicit approval.
---

# Create PR

Create a focused pull request for `Almighty-Shogun/shogun-devpack`.

The user may invoke this skill with or without a branch parameter:

```text
$create-pr
$create-pr fix/ai-terminal-session
```

## Rules

- Accept only optional `fix/*` or `feat/*` branch parameters. Do not invent other prefixes.
- If no branch parameter is provided and the current branch is already `fix/*` or `feat/*`, use the current branch.
- If no branch parameter is provided and the current branch is not `fix/*` or `feat/*`, infer a concise `fix/<slug>` or `feat/<slug>` branch name from the requested work and local changes.
- Ask the user for a branch name when it is unclear whether the work is a fix or feature, or when the branch summary cannot be inferred confidently.
- Do not inspect previous PRs just to infer writing style. Use the PR writing rules in this skill.
- Do not create a PR until the user approves the proposed title and body.
- Do not manually stage or commit changes when the user-level `$commit` skill is available. Invoke `$commit` after determining the intended file scope.
- Do not use `git add .`, `git add -A`, `git reset --hard`, force-push, rebase, or destructive cleanup commands.
- Do not create release tags, version commits, or run publish commands.

## Prepare Branch

Move to the repository root:

```bash
cd "$(git rev-parse --show-toplevel)"
```

Inspect the current state:

```bash
git status --short --branch
git branch --show-current
git diff --stat
git diff --name-status
git diff --cached --stat
git diff --cached --name-status
```

Resolve the target branch name:

- If the user provided a branch parameter, use it as `<branch>`.
- If no branch parameter was provided and the current branch starts with `fix/` or `feat/`, use the current branch as `<branch>`.
- If no branch parameter was provided and the current branch does not start with `fix/` or `feat/`, infer `<branch>` from the work.
- Use `fix/<slug>` for bug fixes, regressions, broken behavior, or reliability fixes.
- Use `feat/<slug>` for new user-facing behavior or capabilities.
- Keep inferred slugs lowercase, hyphen-separated, and concise.
- If the branch type or slug is unclear, ask the user for the branch name and stop.

Validate the resolved branch name:

- Allow only a normal Git branch name with no whitespace.
- Require the branch name to start with `fix/` or `feat/`.
- Reject names containing `..`, `@{`, `//`, leading `-`, trailing `/`, or `.lock`.
- Reject protected branch names such as `main`, `master`, `development`, or `release`.

Check whether the branch already exists:

```bash
git rev-parse --verify "<branch>"
git ls-remote --heads origin "<branch>"
```

If the current branch already matches `<branch>`, continue. If the branch exists locally or remotely and the current branch does not match it, stop and ask whether to switch to that branch, use another name, or cancel. Otherwise, create it:

```bash
git switch -c "<branch>"
```

If the working tree already has uncommitted changes, keep them in place and create the branch from the current `HEAD`. Do not stash or switch through another base branch unless the user explicitly asks.

## Determine Commit Scope

Read the relevant diffs before deciding what belongs in the PR:

```bash
git diff -- <path>
git diff --cached -- <path>
```

For untracked files, inspect their contents before including them. Exclude build outputs, archives, logs, caches, secrets, and unrelated local files unless they are clearly intended.

State the intended scope before invoking `$commit`:

```text
This PR should include:
- <file or group>: <why it belongs>

This PR should not include:
- <file or group>: <why it is unrelated>
```

If the scope is ambiguous, ask the user which files should be included. If the scope is clear, continue.

## Invoke Commit Skill

After the branch exists and the intended file scope is clear, invoke the user-level `$commit` skill.

Invoke the user-level `$commit` skill when available. Let that skill handle staging, commit grouping, verification, commit creation, and push confirmation. If the user requested an automatic flow, pass the appropriate `$commit` flags. Otherwise, invoke `$commit` without flags.

Do not create the PR until the commit skill has completed and the branch has been pushed to `origin`.

After the commit skill finishes, verify:

```bash
git status --short --branch
git log --oneline origin/main..HEAD
git diff --name-status origin/main..HEAD
```

If there are no commits ahead of `origin/main`, stop because there is nothing to open a PR for.

## Generate PR Text

Use this personal PR style without fetching old PRs:

- Title format: `<Kind>: <short human summary>`.
- Use `Bugfix` for fixes, `Feature` for new behavior, `Improvement` for behavior improvements, and `Chore` for maintenance-only changes.
- Keep the title direct and short, for example `Bugfix: AI terminal session handling`.
- Do not use conventional commit prefixes such as `fix:` in the PR title.
- Body should be two or three short paragraphs.
- First paragraph starts with `This PR resolves`, `This PR adds`, `This PR updates`, or `This PR improves`.
- For a bugfix, second paragraph should explain the previous behavior with `Before this, ...` and then the new behavior with `This now ...`.
- Final paragraph should explain scope or safety with `This also ...` or `This keeps ...`.
- Do not add markdown headings, checklists, or test sections unless the user asks.

Example body shape:

```markdown
This PR resolves an issue where <problem>.

Before this, <old behavior>. This now <new behavior>.

This also keeps <important scope or safety detail>.
```

Write the proposed body to a temporary file, for example:

```text
/tmp/shogun-devpack-pr-<branch-slug>.md
```

Show the user:

- branch name
- commits included
- files included
- proposed title
- proposed body
- exact `gh pr create` command

Ask:

```text
Do you want me to create this PR?
```

## Create PR

After the user clearly approves, check that no PR already exists for the branch:

```bash
gh pr view "<branch>" --json url -q .url
```

If a PR already exists, report the URL and stop. Otherwise, create the PR:

```bash
gh pr create \
    --base main \
    --head "<branch>" \
    --title "<title>" \
    --body-file "<body-file>"
```

After creating the PR, report the PR URL from the command output. Do not merge the PR.
