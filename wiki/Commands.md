# Commands

Neo Vitae adds a single command, **`/neovitae`**, that groups admin and debugging tools for its systems: altars, the Anima (Soul) network, Spiritus aura, rituals, meteors, Sentient Armor, and routing.

> **Every `/neovitae` subcommand requires operator permission / Cheats enabled.** These are admin, pack-testing, and debugging utilities, not part of normal progression; ordinary players cannot run them.

## Subcommands

| Command | What it does |
|---------|--------------|
| `/neovitae altar [tier]` | Builds a complete Ara Vitae multiblock at your feet, the highest available tier by default or the tier number you pass. Rune slots are auto-filled with a fixed mix (efficiency, acceleration, speed, augmented capacity, dislocation, then sacrifice). |
| `/neovitae anima-network <player> query` | Show that player's Anima (Soul Network) Essentia Vitae and orb tier. |
| `/neovitae anima-network <player> set <amount>` | Set that player's stored Essentia Vitae. |
| `/neovitae anima-network <player> add <amount>` | Add (or subtract) Essentia Vitae on that player's network. |
| `/neovitae anima-network <player> reset` | Clear that player's Anima network. |
| `/neovitae aura get [all\|<type>]` | Read the current chunk's Spiritus aura, all aspects or a single one. |
| `/neovitae aura set <type\|all> <amount>` | Set a Spiritus aspect (or all) in the chunk, clamped to the configured maximum. |
| `/neovitae aura add <type> <amount>` | Add Spiritus of an aspect to the chunk. |
| `/neovitae aura clear` | Remove all Spiritus from the chunk. |
| `/neovitae ritual <pos> info` | Describe the ritual at the Master Ritual Stone at `<pos>`. |
| `/neovitae ritual <pos> set <ritual>` | Force the Master Ritual Stone at `<pos>` to a ritual, with no activation cost. |
| `/neovitae ritual <pos> stop` | Force-stop the ritual at `<pos>`. |
| `/neovitae ritual <pos> cooldown <ticks>` | Set the cooldown of the ritual at `<pos>`. |
| `/neovitae ritual list` | List every registered ritual id. |
| `/neovitae imperfect <pos> set <ritual>` | Place the required block and activate an imperfect ritual at `<pos>`. |
| `/neovitae imperfect list` | List the imperfect rituals and their required blocks. |
| `/neovitae meteor <pos> <catalyst> [detonatePos]` | Summon a meteor of the given `<catalyst>` item, falling from `<pos>`. It craters on impact by default; pass an optional `<detonatePos>` to detonate it at that position's height instead (the meteor falls straight down, so only the detonation Y takes effect). The catalyst must match a meteor recipe. |
| `/neovitae upgrade <player> ...` | Inspect and edit a player's Sentient Armor: per-upgrade levels (`set`/`get`), point `limits`, the allow/deny `mode`, and `points` recalculation. |
| `/neovitae routing rescan` | Emergency rebuild of a routing Master's network from the live block entities in range. |
| `/neovitae setorbfill <amount>` | Set the fill level of the Blood Orb held in your hand (player only). |
| `/neovitae generate-materials` | Pack/dev tool: scan registered materials and (re)generate the data-driven ore/gravel/dust families. Needs a restart to apply, and runs only on an integrated (single-player) server, not a dedicated one. |
| `/neovitae showcase` | Developer tool: lay out an item/block showcase for screenshots. |
| `/neovitae dungeon-showcase` | Developer tool: place the dungeon's structures with pre-configured structure blocks (in SAVE mode) for editing or capture. |
| `/neovitae stream <preset>` | Developer tool: spawn a test particle "stream" effect from a named preset (e.g. bloodTendril, soulSiphon, voidTendril). |

## Notes

- `query`/`get`/`info`/`list` are read-only and safe to use for diagnostics; the `set`/`add`/`reset`/`clear` variants modify world or player state and should be used with care.
- Because everything is gated behind permission level 2, these commands also work from the server console and command blocks set to the appropriate permission.
