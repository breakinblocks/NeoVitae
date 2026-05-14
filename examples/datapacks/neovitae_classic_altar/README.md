# Classic Altar Layout Pack

This datapack restores the original Blood Magic altar geometry: square rune
rings at each tier with pillars + capstones at the four corners. Drop the
`neovitae_classic_altar` folder into a world's `datapacks/` directory (or
into a global datapack location) and enable it to replace NeoVitae's default
circular tier layouts.

## What it overrides

Six entries in the `neovitae:altar_tier` datapack registry, one per tier:

- `data/neovitae/neovitae/altar_tier/weak.json`
- `data/neovitae/neovitae/altar_tier/apprentice.json`
- `data/neovitae/neovitae/altar_tier/mage.json`
- `data/neovitae/neovitae/altar_tier/master.json`
- `data/neovitae/neovitae/altar_tier/archmage.json`
- `data/neovitae/neovitae/altar_tier/transcendent.json`

Each file lists the structure components for that tier (block positions,
block-or-tag matcher, and whether the slot accepts a rune upgrade).

## How the multiblock preview stays in sync

NeoVitae builds the in-book multiblock preview from the loaded altar tier
data at server start, so this pack also reshapes the Scriptura Vitae book
diagrams. There is no separate Modonomicon multiblock JSON to author; edit
the altar tier and the preview follows.

If you need to fully override the preview (for example, to add cosmetic
display-only blocks that the validator ignores), drop a Modonomicon
multiblock JSON at `data/neovitae/modonomicon/multiblocks/altar_<one|two
|three|four|five|six>.json` and the runtime will leave it untouched.

## Authoring your own pack

Use these files as a starting template. The `components` array drives both
the structural check and the rendered preview, so:

- `pos`: integer XYZ offset from the altar block.
- `valid`: a single block id (`neovitae:ara_vitae`) or a tag reference
  (`#neovitae:altar/runes`).
- `upgrade`: `true` if a player-installed rune lives there (the slot
  cycles through rune blocks in the preview), `false` for structural
  blocks (rendered as the blank rune or the tag's first member).
- `optional`: optional, defaults to `false`. When `true`, the position
  validates as either air or the configured `valid` matcher; this is
  how the bundled tiers leave the pillar columns under each cap as
  decoration rather than required structural blocks.

Tag references resolve against the live block tags, so a pack can keep
the same skeleton and just retag which blocks count as runes or pillars.

## Tier visual effects

The optional `effects` array attaches per-tier capstone visuals. Each entry
is rendered cumulatively (a tier 5 altar plays its own effects plus every
lower tier's), and pack authors can re-aim or restyle them by editing the
JSON; nothing else needs to change.

```json
{
  "type": "cap_orbit_life_pulse",
  "color": 12268288,
  "origins": [[3, 1, 3], [3, 1, -3], [-3, 1, 3], [-3, 1, -3]]
}
```

- `type`: one of
  - `cap_orbit_life_pulse` - 4 caps orbit then fire a life-pulse stream into
    the altar.
  - `cap_orbit_spiral_staggered` - per-cap orbit with staggered phase, ends
    in a spiralling stream.
  - `cap_burst` - low-rate ambient particle bursts at each origin.
  - `cap_crystal_cascade` - downward cascading column above each origin.
  - `cap_render_hover_array` - client-side rotating alchemy-array texture
    hovering above each origin.
- `origins`: one or more XYZ offsets from the altar block; each becomes a
  particle / render origin.
- `color`: tint colour packed as 0xRRGGBB (decimal). Optional, defaults to
  white.

Omit the `effects` field entirely to ship a tier with no capstone visuals.
