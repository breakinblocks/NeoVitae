# Pack Makers Overview

NeoVitae is **datapack-driven from the ground up**. Almost every balance lever (orb capacities, sigil costs, ritual prices, ranges, altar geometry, recipes, tags, loot, anointments, materials) is exposed as JSON. Pack makers can tune the entire mod without writing a line of Java.

This page is the entrypoint. Use the sub-pages for schemas and examples.

## Index

- **[Pack Makers DataMaps and Recipes](Pack-Makers-DataMaps-and-Recipes)**. Every DataMap (orb / sigil / ritual / imperfect-ritual / spiritus-gem / dungeon-ore-weights), sigil effect definitions, altar-tier geometry, every recipe type (Ara Vitae, Tabula Vitae, Hellfire Forge, Athanor, Alchemy Array, Meteor), tags, loot modifiers, Sentient Armor upgrades, Curios integration, custom player attributes.
- **[Pack Makers Materials and Dungeons](Pack-Makers-Materials-and-Dungeons)**. The data-driven material system (`config/neovitae/materials.json`), KubeJS event hooks, custom dungeon rooms and room pools, the telepose blacklist tags.
- **[API Overview](API-Overview)** and **[API Reference](API-Reference)**. Java-side interfaces for true addon mods.

For the player-facing systems behind each customizable feature, cross-reference the appropriate page:

- [Ara Vitae and Runes](Ara-Vitae-and-Runes), [Blood Orbs and Anima](Blood-Orbs-and-Anima), [Sigils](Sigils), [Rituals](Rituals), [Sentient Armor](Sentient-Armor), [Tabula Vitae, Flasks and Anointments](Tabula-Vitae-Flasks-and-Anointments), [Hellfire Forge and Sentient Equipment](Hellfire-Forge-and-Sentient-Equipment), [Athanor and Materials](Athanor-and-Materials), [Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals), [Alchemy Arrays](Alchemy-Arrays).
- [The Endless Dungeon](The-Endless-Dungeon), [Daemonium Bestiary](Daemonium-Bestiary), [Dungeon Mechanics](Dungeon-Mechanics).

## Getting Started

NeoVitae uses NeoForge's DataMap system for most customizations. To customize the mod:

1. Create a datapack in your modpack's `datapacks/` folder, or use KubeJS' `data/` tree.
2. Create the appropriate directory structure under `data/neovitae/`.
3. Add or modify JSON files as documented in the sub-pages.

### Directory Layout

```
your_datapack/
├── pack.mcmeta
└── data/
    └── neovitae/
        ├── data_maps/
        │   ├── block/
        │   │   └── dungeon_ore_weights.json
        │   ├── item/
        │   │   ├── blood_orb_stats.json
        │   │   ├── sigil_stats.json
        │   │   └── spiritus_gem_max.json
        │   └── neovitae/
        │       ├── ritual/
        │       │   └── ritual_stats.json
        │       └── imperfect_ritual/
        │           └── imperfect_ritual_stats.json
        ├── neovitae/
        │   ├── altar_tier/
        │   └── sigil_type/
        ├── recipe/
        ├── tags/
        ├── loot_table/
        └── loot_modifiers/
```

Schematic JSON for dungeon rooms lives under `assets/<namespace>/schematics/`, not `data/`. See [Pack Makers Materials and Dungeons](Pack-Makers-Materials-and-Dungeons) for the full dungeon authoring flow.

## Tips and Best Practices

1. **Use `"replace": false`** in tags to add to existing lists instead of replacing them.
2. **Test incrementally**. Make one change at a time and verify it works.
3. **Check the logs**. NeoVitae logs warnings for invalid configurations.
4. **Use JEI/REI** to verify recipe changes are applied.
5. **Backup your world** before testing major balance changes.
6. **DataMaps are synced to clients** automatically, so JEI displays match server values.
7. **Recipe and tag changes need `/reload`**; some material-system changes need a full restart.

## Developer Tools

### Ritual Designer

`neovitae:ritual_designer` is an OP-gated developer item used to capture an in-world rune layout and emit the matching `gatherComponents` Java snippet for a new `Ritual` subclass. It has no crafting recipe; grant it with `/give @s neovitae:ritual_designer`. All actions require permission level 2; survival players get a refusal message.

**Workflow:**

1. Build the desired ritual: place a Master Ritual Stone at the centre, then arrange any of the seven `*_ritual_stone` blocks around it.
2. Hold the Ritual Designer. **Sneak + Right-click** any block to mark **Corner 1**, then **Sneak + Right-click** a second block to mark **Corner 2**. The two corners define the AABB to scan. Marking a third corner resets Corner 1.
3. **Right-click the Master Ritual Stone**. The scanner walks every position in the AABB, recording only blocks whose class is one of the seven `BlockRitualStone` variants (mapped to `EnumRuneType.BLANK/WATER/FIRE/EARTH/AIR/DUSK/DAWN`). Air and any non-rune block, including the master stone itself, are ignored.
4. The generated method body is delivered to the operator's client via the `neovitae:ritual_code` payload and copied to the system clipboard. The same lines are also echoed in chat between `=== RITUAL CODE START ===` / `=== RITUAL CODE END ===` markers so they survive if the clipboard fails.
5. **Sneak + Right-click in air** clears both corners and plays an extinguish sound.

**Output format:**

The emitted snippet always uses `addRune(...)` with positions relative to the master stone. When every Y layer in the scan contains the same `(x, z, rune)` set, the generator collapses them into a `for (int layer = lo; layer <= hi; layer++)` loop; otherwise the runes are emitted as individual `addRune` calls sorted by `y`, then `x`, then `z`.

```java
@Override
public void gatherComponents(Consumer<RitualComponent> components) {
    addRune(components, 1, 0, 0, EnumRuneType.WATER);
    addRune(components, -1, 0, 0, EnumRuneType.WATER);
    addRune(components, 0, 0, 1, EnumRuneType.FIRE);
    addRune(components, 0, 0, -1, EnumRuneType.FIRE);
}
```

**Conflict detection:** before printing, the scanned `(offset, runeType)` set is compared against every registered ritual's layout (honouring datapack `neovitae:ritual_layout` overrides). If an identical layout is already registered, the tool aborts and reports the colliding ritual's id so authors can perturb the pattern.

**Tooltip and state:** the item tooltip lists the control scheme, highlights that OP is required, and shows the currently-stored Corner 1 / Corner 2 coordinates. Corner positions are stored on `neovitae:ritual_corner1` / `neovitae:ritual_corner2` `BlockPos` data components on the stack itself, so different operators can keep independent selections.

## Console Commands

All admin/debug functionality is exposed under the single `/neovitae` root. Every subcommand requires permission level 2 (operator / gamemaster). The legacy `/nv-*`, `/anima`, and `/sentient-upgrade` standalone entrypoints have been removed.

| Command | What it does |
|---------|--------------|
| `/neovitae altar` | Places a max-tier Ara Vitae at the player's feet and fills the multiblock around it. Rune slots are filled in a fixed mix (10 efficiency, 19 acceleration, 9 speed, 15 augmented capacity, 22 dislocation, remainder sacrifice). |
| `/neovitae anima-network <player> query` | Print the player's current Anima EV. |
| `/neovitae anima-network <player> reset` | Set the player's Anima EV to 0. |
| `/neovitae anima-network <player> set <amount>` | Set the player's Anima EV to an exact amount. |
| `/neovitae anima-network <player> add <amount>` | Add EV to the player's Anima. |
| `/neovitae aura get [type\|all]` | Show spiritus aura in the player's current chunk for one type or all. |
| `/neovitae aura set <type\|all> <amount>` | Overwrite the aura amount for a type (clamped to per-chunk max). |
| `/neovitae aura add <type\|all> <amount>` | Add/subtract from the aura amount (negative subtracts). |
| `/neovitae aura clear` | Zero out every aspect in the current chunk. |
| `/neovitae dungeon-showcase` | Place every registered dungeon structure NBT in a grid for visual review. |
| `/neovitae generate-materials` | Scan all installed `c:ores/*` tags, auto-discover new ore materials, append them to `config/neovitae/materials.json`, and report what was added. Restart required to load new items. |
| `/neovitae imperfect <pos> set <ritual_id>` | Place the required activation block above the imperfect ritual stone at `<pos>` and trigger the ritual. |
| `/neovitae imperfect list` | List every registered imperfect ritual with its required catalyst block. |
| `/neovitae ritual <pos> info` | Print the running ritual (if any), its tick, and current EV for the MRS at `<pos>`. |
| `/neovitae ritual <pos> stop` | Force-stop the ritual at `<pos>` without consuming components. |
| `/neovitae ritual <pos> set <ritual_id>` | Force a specific ritual onto the MRS at `<pos>` (skips activation cost). |
| `/neovitae ritual <pos> cooldown <ticks>` | Override the current ritual's cooldown timer. |
| `/neovitae ritual list` | Print every registered ritual id. |
| `/neovitae routing rescan` | Rebuild the connection graph of the Master Routing Node you are looking at (or the nearest one within 16 blocks). Scans 32 blocks for nodes. |
| `/neovitae setorbfill <amount>` | Set the internal fluid amount of the Blood Orb in your main hand (clamped to capacity). |
| `/neovitae showcase` | Place a wall of every NeoVitae block, an item-frame wall of every NeoVitae item, every ritual layout, every imperfect ritual, and every altar tier in front of you. |
| `/neovitae stream <preset>` | Fire one of the `StreamPresets` particle/visual presets from the player toward their look-target. Useful for debugging stream visuals. |
| `/neovitae upgrade <player> upgrade set <id> <exp>` | Give a Sentient Armor upgrade to the player's chest piece with the given experience. |
| `/neovitae upgrade <player> upgrade get [id]` | List upgrade XP on the player's chest piece (one upgrade or all). |
| `/neovitae upgrade <player> limits set <id> <exp>` | Set a per-upgrade XP cap on the player. |
| `/neovitae upgrade <player> limits get [id]` | Read the per-upgrade XP cap. |
| `/neovitae upgrade <player> limits remove <id>` | Remove an upgrade's per-player cap. |
| `/neovitae upgrade <player> limits mode {allow\|deny}` | Switch the per-player limits map between allow-list and deny-list semantics. |
| `/neovitae upgrade <player> points recalc` | Recompute the player's available upgrade-point pool. |
| `/neovitae upgrade <player> points set-cap <n>` | Override the player's upgrade-point cap. |
| `/neovitae upgrade <player> points set-cap default` | Reset the cap to the server config's `DEFAULT_UPGRADE_POINTS`. |
| `/neovitae upgrade <player> points set-cap evolved` | Set the cap to the server config's `EVOLUTION_UPGRADE_POINTS` (evolved-armor tier). |

## Compatibility Notes

- All datamaps are synced to clients automatically.
- Recipe changes require world reload (`/reload`).
- Tag changes require world reload.
- Material-system changes (`config/neovitae/materials.json`) require a full game restart; items are generated at startup.

## Getting Help

- GitHub Issues: <https://github.com/breakinblocks/NeoVitae/issues>
- Reference datapacks: check `src/generated/resources/` in the NeoVitae repo for every JSON the mod ships.
- Example pack: `examples/datapacks/neovitae_classic_altar/` in the repo ships a complete square-layout altar tier definition and is the easiest starting point for authoring altar tiers.
