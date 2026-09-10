# Documentation

All project documentation is stored here as Markdown files and is **synchronized
both ways** with the [GitHub Wiki](https://github.com/Strogolsky/proFinder_backend/wiki).

## How the sync works

The [`.github/workflows/wiki-sync.yml`](https://github.com/Strogolsky/proFinder_backend/blob/develop/.github/workflows/wiki-sync.yml)
workflow uses [`newrelic/wiki-sync-action`](https://github.com/newrelic/wiki-sync-action):

| Trigger | Direction |
| --- | --- |
| Push to `develop` that touches `docs/**` | `docs/` &rarr; Wiki |
| Wiki page edited in the GitHub UI (`gollum` event) | Wiki &rarr; `docs/` (commit to `develop`) |

So you can edit either the files in this folder (via a pull request) or the Wiki
pages directly in the browser — the other side is updated within a minute.

## File / page naming

GitHub Wiki maps file names to page titles 1:1 (the sync action does **not** rename
files or rewrite links):

* `Home.md` is the Wiki landing page — keep it.
* `_Sidebar.md` / `_Footer.md` render as the Wiki sidebar / footer.
* `1 - Description.md` becomes the page **"1 - Description"**; cross-links use the
  URL-encoded relative form `[Description](1%20-%20Description.md)` — literally
  `%20` for each space. Heading anchors (`#some-heading`) use GitHub's slug rules
  and work on both sides.
* Subfolders are flattened into the page name by the action, so keep `docs/` flat.

## Migrated from Obsidian

The pages here came from an Obsidian vault. What was done / to keep in mind:

* File names were kept as-is (numbered `N - Title.md`) so the ~400 existing
  cross-links keep working. Links use the plain Markdown link form with
  `%20`-encoded spaces in the target — no Obsidian `[[wikilinks]]` or `![[embeds]]` were used,
  so nothing to convert.
* No YAML frontmatter in these files — nothing to strip.
* Fenced `mermaid` code blocks (files 5, 6, 10) render natively on GitHub in both
  the repo and the Wiki.
* The Excalidraw file (`*.excalidraw.md`) was **excluded** — it only renders inside
  Obsidian. Export diagrams to SVG/PNG into `docs/img/` and embed them with a
  standard Markdown image tag if they are needed.
* Obsidian callouts `> [!NOTE]` would degrade to a plain blockquote on the Wiki
  (acceptable) — none are currently used.

## One-time setup (already done, kept here for reference)

1. The Wiki must be initialized once — create any page in the GitHub Wiki UI so the
   `<repo>.wiki.git` repository exists.
2. A repository secret **`WIKI_ACTION_TOKEN`** must exist: a Personal Access Token
   with write access to this repository (classic PAT with `repo` scope, or a
   fine-grained PAT with *Contents: read and write*). The default `GITHUB_TOKEN`
   is not used because pushes made with it do not trigger other workflows and it
   cannot push wiki-to-repo on the `gollum` event.
3. The workflow file must be on the repository's **default branch** (`develop`) for
   the `gollum` trigger to fire.
