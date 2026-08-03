#!/usr/bin/env python3
"""Port Crowdin translations from the main branch down to dev (1.21.1).

Crowdin translates a single source file: main's generated en_us.json. This
script regenerates dev's translation files from main's, remapping the keys
whose names are forced to differ by the Minecraft version.

Keys that exist only on main are carried over anyway: Minecraft ignores
translation keys nothing references, and pre-staging them means a feature
backported to dev already has its translations.

A key whose *English* differs between the two branches is skipped, because a
translation made against main's wording would silently misdescribe dev's
behaviour. Any such key keeps whatever dev already had.

Usage (from the repo root):
    python tools/port-lang.py                  # write dev's lang files
    python tools/port-lang.py --check          # report only, exit 1 on drift
    python tools/port-lang.py --source-ref X   # read from ref X instead of main
"""

import argparse
import collections
import json
import pathlib
import subprocess
import sys

SOURCE_REF = "main"
TARGET_REF = "dev"
SOURCE_EN_US = "src/generated/resources/assets/neovitae/lang/en_us.json"
TARGET_EN_US = "src/generated/resources/assets/neovitae/lang/en_us.json"
LANG_DIR = "src/main/resources/assets/neovitae/lang"
KEY_MAP = "tools/lang-key-map.json"


def git_show(ref, path):
    out = subprocess.run(
        ["git", "show", f"{ref}:{path}"],
        capture_output=True, check=False, encoding="utf-8",
    )
    if out.returncode != 0:
        return None
    return out.stdout


def load_json(text):
    return json.loads(text, object_pairs_hook=collections.OrderedDict)


def list_lang_files(ref):
    out = subprocess.run(
        ["git", "ls-tree", "-r", "--name-only", ref, LANG_DIR],
        capture_output=True, check=True, encoding="utf-8",
    )
    names = []
    for line in out.stdout.splitlines():
        name = pathlib.PurePosixPath(line).name
        if name.endswith(".json") and name != "en_us.json":
            names.append(name)
    return sorted(names)


def dump(obj):
    return json.dumps(obj, indent=2, ensure_ascii=False) + "\n"


def current_branch():
    out = subprocess.run(
        ["git", "rev-parse", "--abbrev-ref", "HEAD"],
        capture_output=True, check=True, encoding="utf-8",
    )
    return out.stdout.strip()


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--source-ref", default=SOURCE_REF)
    ap.add_argument("--target-ref", default=TARGET_REF)
    ap.add_argument("--check", action="store_true")
    ap.add_argument("--force", action="store_true")
    args = ap.parse_args()

    branch = current_branch()
    if branch != args.target_ref and not args.force:
        print(
            f"refusing to run: this writes {args.target_ref}'s translation files, "
            f"but the working tree is on '{branch}'.\n"
            f"run 'git checkout {args.target_ref}' first, or pass --force.",
            file=sys.stderr,
        )
        return 2

    key_map = load_json(pathlib.Path(KEY_MAP).read_text(encoding="utf-8"))["map"]

    src_en = load_json(git_show(args.source_ref, SOURCE_EN_US))
    tgt_en = load_json(git_show(args.target_ref, TARGET_EN_US))

    diverged = set()
    for src_key, src_val in src_en.items():
        tgt_key = key_map.get(src_key, src_key)
        if tgt_key in tgt_en and tgt_en[tgt_key] != src_val:
            diverged.add(src_key)

    if diverged:
        print(f"! {len(diverged)} key(s) have different English on each branch; keeping dev's existing translation:")
        for k in sorted(diverged):
            print(f"    {k}")

    lang_dir = pathlib.Path(LANG_DIR)
    drift = False
    for name in list_lang_files(args.source_ref):
        src = load_json(git_show(args.source_ref, f"{LANG_DIR}/{name}"))
        existing_raw = git_show(args.target_ref, f"{LANG_DIR}/{name}")
        existing = load_json(existing_raw) if existing_raw else {}

        out = collections.OrderedDict()
        for src_key, val in src.items():
            tgt_key = key_map.get(src_key, src_key)
            if src_key in diverged:
                if tgt_key in existing:
                    out[tgt_key] = existing[tgt_key]
                continue
            out[tgt_key] = val

        for key, val in existing.items():
            if key not in out and key not in src_en and key not in key_map.values():
                out[key] = val

        text = dump(out)
        path = lang_dir / name
        current = path.read_text(encoding="utf-8") if path.exists() else None
        if current == text:
            print(f"  {name}: up to date ({len(out)} keys)")
            continue

        drift = True
        if args.check:
            print(f"  {name}: OUT OF DATE ({len(out)} keys)")
        else:
            path.write_text(text, encoding="utf-8", newline="\n")
            print(f"  {name}: written ({len(out)} keys)")

    if args.check and drift:
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
