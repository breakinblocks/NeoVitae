#!/usr/bin/env bash
# Sync wiki/ to the GitHub wiki repo.
#
# Reads wiki/*.md (and any other tracked assets) from the current main-repo
# checkout, mirrors them into a fresh clone of <repo>.wiki.git, and pushes.
#
# Idempotent: if nothing changed in wiki/, it's a no-op push.
#
# Treats wiki/ as the canonical source — any pages added/edited directly on
# GitHub will be overwritten on the next sync. Don't edit the wiki via the
# web UI; edit wiki/ locally instead.
#
# Usage:
#   tools/sync-wiki.sh           # sync now
#   tools/sync-wiki.sh --dry-run # report what would change, don't push

set -euo pipefail

DRY_RUN=0
if [ "${1:-}" = "--dry-run" ]; then
    DRY_RUN=1
fi

if [ ! -d "wiki" ]; then
    echo "No wiki/ directory in cwd. Run from the repo root." >&2
    exit 1
fi

ORIGIN=$(git remote get-url origin)
WIKI_URL="${ORIGIN%.git}.wiki.git"

TMP=$(mktemp -d)
trap "rm -rf '$TMP'" EXIT

echo "→ Cloning $WIKI_URL"
if ! git clone --quiet "$WIKI_URL" "$TMP" 2>/dev/null; then
    cat >&2 <<EOF
Could not clone $WIKI_URL.

The GitHub wiki repo only exists once at least one page has been created.
Visit https://github.com/${ORIGIN#*github.com[/:]} (drop .git), click the Wiki
tab, and create any page (e.g. paste wiki/Home.md). Then re-run this script.
EOF
    exit 1
fi

# Mirror wiki/ → clone, deleting any files that no longer exist locally.
# Preserve the clone's .git directory.
echo "→ Mirroring wiki/ → wiki repo"
find "$TMP" -mindepth 1 -maxdepth 1 -not -name '.git' -exec rm -rf {} +
cp -r wiki/. "$TMP/"

cd "$TMP"

if [ -z "$(git status --porcelain)" ]; then
    echo "✓ Wiki already up to date — nothing to push"
    exit 0
fi

echo "→ Changes detected:"
git status --short | sed 's/^/    /'

if [ "$DRY_RUN" = "1" ]; then
    echo "✓ Dry run — not committing"
    exit 0
fi

git add -A
git -c user.name="$(git -C "$OLDPWD" config user.name)" \
    -c user.email="$(git -C "$OLDPWD" config user.email)" \
    commit --quiet -m "Sync wiki from main repo"
git push --quiet
echo "✓ Wiki updated"
