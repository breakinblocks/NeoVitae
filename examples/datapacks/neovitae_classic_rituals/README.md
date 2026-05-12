# Ritual Layout Example Pack

A minimal worked example of the `neovitae:ritual_layout` datapack registry.
Ships the default Well of Suffering layout — copy the file, rename it to
the ritual you want to retune, and edit the rune positions. The mod falls
back to its hardcoded Java layout for any ritual that doesn't have a JSON
entry, so you only need to ship overrides for the rituals you actually
change.

## What an entry looks like

```json
{
  "components": [
    { "pos": [1, 0, 1], "rune": "water" },
    { "pos": [1, 0, -1], "rune": "water" },
    { "pos": [-1, 0, -1], "rune": "water" },
    { "pos": [-1, 0, 1], "rune": "water" }
  ]
}
```

- `pos`: integer XYZ offset from the master ritual stone.
- `rune`: one of `blank`, `water`, `fire`, `earth`, `air`, `dusk`, `dawn`.

The file's path is `data/neovitae/neovitae/ritual_layout/<ritual_id>.json`
where `<ritual_id>` matches the ritual's registry path (e.g. `water`,
`well_of_suffering`, `crystallum_fractura`).

## How the multiblock preview stays in sync

NeoVitae builds the Modonomicon in-book preview from the loaded ritual
layout at server start, so editing the JSON in this pack also reshapes the
Scriptura Vitae diagram for that ritual. To override the preview only
(without changing the structure check), drop a Modonomicon multiblock JSON
at `data/<ns>/modonomicon/multiblocks/ritual/<ritual_id>.json` and the
runtime will keep that file instead of generating its own.

## Constraints

- The master ritual stone is always at `[0, 0, 0]` and must not appear in
  `components`; the structure check assumes it.
- A ritual whose layout JSON is missing or has an empty `components` list
  falls through to the hardcoded Java default, so partial packs are safe.
- Rune block matching uses the seven `EnumRuneType` values listed above;
  pack authors who want to swap which *block* counts as a "water rune"
  would need a follow-up change (currently the block-id-per-rune is wired
  in Java).
