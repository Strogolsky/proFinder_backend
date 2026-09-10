# Documentation

All project documentation is stored here as Markdown files and is **synchronized
both ways** with the [GitHub Wiki](https://github.com/Strogolsky/proFinder_backend/wiki).

## How the sync works

The [`.github/workflows/wiki-sync.yml`](../.github/workflows/wiki-sync.yml) workflow
uses [`newrelic/wiki-sync-action`](https://github.com/newrelic/wiki-sync-action):

| Trigger | Direction |
| --- | --- |
| Push to `develop` that touches `docs/**` | `docs/` &rarr; Wiki |
| Wiki page edited in the GitHub UI (`gollum` event) | Wiki &rarr; `docs/` (commit to `develop`) |

So you can edit either the files in this folder (via a pull request) or the Wiki
pages directly in the browser — the other side is updated within a minute.

## File / page naming

GitHub Wiki maps file names to page titles:

* `Home.md` is the Wiki landing page — keep it.
* `Some-Page.md` becomes the page **"Some Page"** at `.../wiki/Some-Page`.
* `_Sidebar.md` / `_Footer.md` render as the Wiki sidebar / footer.
* Use `[[Page Title]]` or `[[text|Page-Name]]` for links between Wiki pages;
  they also work as relative links when browsed in the repo.

Subfolders are supported but flattened into the page name by the action, so prefer
a flat layout with `-` separated names.

## Migrating from Obsidian

Mostly works, because both Obsidian and GitHub Wiki understand `[[wikilinks]]`.
Watch out for the following and fix them before / during the move:

* **Flatten the vault.** Put every note directly in `docs/` with unique,
  `Kebab-Case` names. GitHub Wiki is effectively flat and the sync action
  flattens subfolders into the page name.
* **Rename files with spaces.** `My Note.md` &rarr; `My-Note.md`. Update the
  `[[links]]` to match (or keep `[[My Note]]` — Wiki resolves by title, but be
  consistent).
* **Embeds are not supported.** `![[Other Note]]` transclusion does not render on
  GitHub Wiki — inline the content or replace with a plain `[[Other Note]]` link.
* **Images / attachments.** Move them into `docs/` (e.g. `docs/img/`) and use
  standard Markdown: `![alt](img/diagram.png)`. `![[diagram.png]]` will not render.
* **Strip YAML frontmatter** (`---` blocks) or expect it to show up as a table at
  the top of the Wiki page.
* **Callouts** `> [!NOTE]` render as GitHub alerts in the repo but not on the Wiki
  (they degrade to a normal blockquote — acceptable).
* **Plugin syntax** (Dataview, Tasks, Templater, Mermaid via plugin, etc.) will not
  execute. Plain ```mermaid``` fenced blocks do render on GitHub.
* Keep one **`Home.md`** as the landing page.

A safe path: copy the vault into `docs/`, do the renames and link fixes locally,
open a PR, review the rendered Markdown on GitHub, then merge.

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
