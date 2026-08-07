# Translations

Neo Vitae is translated on Crowdin: https://crowdin.com/project/neovitae

`glossary.csv` is the term base for that project.

## Formatting rules

These break the game if changed:

- `%%` must stay `%%`. The book renderer runs strings through a formatter, so a
  literal percent sign is escaped. Writing `%` produces a crash or garbled text.
  No QA check catches this one.
- `%s` and `%d` are substituted at runtime. Keep them, and keep their order
  unless the target language requires reordering (use `%1$s` style if so).
- `[#](8B0000)text[#]()` is book colour markup. Translate `text`, never the
  `[#](...)` wrappers or the hex code.
- `\n` line breaks should be preserved.

Source strings come from the `main` branch and cover both supported Minecraft
versions, so some strings describe features not present in every build.
