# Translations

Neo Vitae is translated on Crowdin. The source of truth is the **`26.1`** branch.

## Why 26.1

`26.1` is a strict superset of `dev`'s strings: it has every key `dev` has, plus
~140 for features that only exist there (routing GUI, Athanor GUI, Jade tooltips,
several commands). Translating `26.1` therefore covers both branches; translating
`dev` would leave those 140 permanently untranslated.

## Layout

| Path | Purpose |
| --- | --- |
| `crowdin.yml` | Source/translation paths and the Crowdin -> Minecraft locale mapping |
| `crowdin/glossary.csv` | Do-not-translate terms and consistency-critical names |
| `.github/workflows/crowdin.yml` | Uploads sources on change, opens a translation PR weekly |
| `tools/port-lang.py` | Regenerates `dev`'s translation files from `26.1`'s |
| `tools/lang-key-map.json` | The keys whose names differ between branches |

The source file is `src/generated/resources/assets/neovitae/lang/en_us.json`,
which is produced by `./gradlew runData` - edit `NVLanguageProvider` and the
book entry classes under `src/datagen/`, never the generated file.

## One-time setup

1. Create a Crowdin project (the Open Source plan is free for public repos).
   Set the source language to English and add the target languages you want.
2. Add two repository secrets: `CROWDIN_PROJECT_ID` and `CROWDIN_PERSONAL_TOKEN`.
3. Import `crowdin/glossary.csv` under **Resources -> Glossary**, mapping the
   columns to Term / Description / Type.
4. Upload the existing `pt_br.json` once so that work is not lost:

       crowdin upload translations --language pt-BR

5. In **Settings -> QA checks**, enable the placeholder and tag checks (see
   *Formatting rules* below).

## Locale codes

Minecraft uses lowercase locale codes with an underscore (`pt_br`, `zh_cn`),
which do not match Crowdin's (`pt-BR`, `zh-CN`). `crowdin.yml` maps them via
`languages_mapping`. **When you add a language in Crowdin, add its row there
too** - without a mapping the file lands at the wrong filename and Minecraft
silently ignores it.

## Formatting rules for translators

These break the game if changed, so they are worth calling out in the Crowdin
project description:

- `%%` must stay `%%`. The book renderer runs strings through a formatter, so a
  literal percent sign is escaped. Writing `%` produces a crash or garbled text.
- `%s` and `%d` are substituted at runtime. Keep them, and keep their order
  unless the target language requires reordering (use `%1$s` style if so).
- `[#](8B0000)text[#]()` is book colour markup. Translate `text`, never the
  `[#](...)` wrappers or the hex code.
- `\n` line breaks should be preserved.

## Porting translations to dev

Crowdin only ever writes to `26.1`. After a translation PR merges:

    git checkout dev
    python tools/port-lang.py

This rewrites `dev`'s files under `src/main/resources/assets/neovitae/lang/`.
Use `--check` for a dry run (exit code 1 means dev is out of date).

The script:

- remaps keys listed in `tools/lang-key-map.json`. Currently one entry:
  `key.category.neovitae.neovitae` (26.1) is `key.categories.neovitae` on dev,
  because 26.1's `KeyMapping.Category.register()` derives the key from the
  namespace while 1.21.1 takes a raw string. This one cannot be reconciled.
- carries over keys that only exist on 26.1. Minecraft ignores translation keys
  nothing references, so they cost nothing, and a feature later backported to
  dev arrives with its translations already in place.
- skips any key whose **English** differs between the branches, keeping dev's
  existing translation instead. A translation written against 26.1's wording
  would otherwise misdescribe dev's behaviour. This is currently zero keys; the
  script prints them if that ever changes, which is a signal the two branches
  have drifted and should be reconciled.
