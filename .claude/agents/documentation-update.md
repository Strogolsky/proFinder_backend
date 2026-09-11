---
name: documentation-update
description: Review and update project documentation to reflect current codebase state
---

# Documentation Update Skill

This skill helps keep project documentation in sync with actual implementation and architecture changes.

## When to Use

Trigger this skill when:
- Major architectural changes are made (new microservices, new workflows, etc.)
- CI/CD pipelines are modified
- New features or patterns are introduced
- Technology stack changes
- Breaking changes to APIs or configuration

## What to Do

### 1. Identify Changed Areas
- Review recent git commits and PRs
- List all files modified in `.github/workflows/`
- Check for changes in `src/main/java/` structure
- Look for new or modified configuration files

### 2. Check Documentation Relevance

For each documentation file in `/docs/`:
- [ ] Is this file still accurate?
- [ ] Does it reference outdated code patterns?
- [ ] Are code examples still valid?
- [ ] Are commands/paths still correct?

Critical files to review first:
- `15 - SDLC & Workflow.md` — CI/CD workflows
- `3 - System Design.md` — Architecture
- `CLAUDE.md` — Project patterns

### 3. Update Documentation

**Update pattern:**
1. Read the documentation file
2. Compare with actual code/configuration
3. Identify discrepancies
4. Update with current state
5. Add changelog entry
6. Commit with clear message

**Example commit message:**
```
docs: update CI/CD pipeline documentation

- Document all 4 workflows: test.yml, quality.yml, e2e.yml, deploy.yml
- Update action versions to v4
- Clarify quality.yml runs only on PR
- Add changelog entry for v1.2
```

### 4. Verify Changes

After updating:
- [ ] All code examples compile/are valid syntax
- [ ] File paths match current structure
- [ ] Commands work as documented
- [ ] Links between docs are valid
- [ ] Changelog entry added with date and version

### 5. Commit and Push

```bash
git add docs/
git commit -m "docs: [what changed]"
git push origin [branch]
```

## Checklist for Documentation Review

### Architecture Docs (1 - Description, 3 - System Design)
- [ ] Services and their responsibilities documented
- [ ] Data flow diagrams accurate
- [ ] Integration points correct
- [ ] External dependencies listed

### SDLC & Workflow (15 - SDLC & Workflow.md)
- [ ] CI/CD workflows match `.github/workflows/` files
- [ ] Branch strategy reflects git setup
- [ ] Testing strategy matches pom.xml configuration
- [ ] Deployment steps valid
- [ ] Changelog updated

### CLAUDE.md
- [ ] Build commands still work
- [ ] Project structure reflects actual layout
- [ ] Code patterns match implementation
- [ ] Tech stack versions match pom.xml
- [ ] Debugging tips still relevant

### API Documentation (8 - API Specification.md)
- [ ] Endpoints match actual REST resources
- [ ] Request/response examples valid
- [ ] Authentication method documented
- [ ] Error codes documented

## Common Issues to Fix

| Issue | Fix |
|-------|-----|
| Outdated version numbers | Update to match pom.xml/GitHub Actions |
| References to deleted files | Remove references or document as removed |
| Incorrect file paths | Run `find` command to verify actual paths |
| Missing new features | Add documentation for recent additions |
| Broken links | Verify all doc-to-doc links work |
| Code examples that don't compile | Test locally and fix syntax |

## Tips

- Always verify code examples by testing locally
- Keep Changelog up-to-date with version and date
- Link between related documentation files
- Use relative paths for internal links
- When major changes happen, update multiple related docs (not just one)

## Related

- [CLAUDE.md](../../CLAUDE.md) — Project guide
- [docs/](/docs/) — All documentation files
- [.github/workflows/](/github/workflows/) — CI/CD definitions
