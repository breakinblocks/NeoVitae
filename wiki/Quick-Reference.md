# Quick Reference

A glossary of every term, resource, and abbreviation you'll encounter.

## Resources

**Blood**. Raw, unrefined vitality torn from living flesh by blade or sacrifice. The crude material the Ara Vitae purifies.

**Essentia Vitae** (EV). Refined blood. A luminous crimson fluid produced by the Ara Vitae. Pools in the altar's basin, may be siphoned by Dislocation Runes, drawn into a bucket, or consumed by altar crafting. **Throughout the mod, "EV" and "Essentia Vitae" are interchangeable.**

**Spiritus**. A separate, parallel resource representing the latent magical energy of the world. Torn from slain creatures wounded by Throwing Daggers (more efficient with a Sentient Sword). Divided into five **Aspects**. See [Spiritus](Spiritus-Aspects-and-Crystals).

**Anima**. Your personal, invisible reservoir of EV. Items bound to you draw from and replenish it across any distance, any world, any dimension. Capacity is set by the tier of your **Orb of Vitae**.

## Containers

**Orb of Vitae**. The vessel that links you to your Anima. Six tiers, each with vastly greater capacity than the last:

| Tier | Name |
| :---: | --- |
| 1 | Novicius Orb of Vitae |
| 2 | Discipulus Orb of Vitae |
| 3 | Magus Orb of Vitae |
| 4 | Dominator Orb of Vitae |
| 5 | Imperator Orb of Vitae |
| 6 | Dominus Orb of Vitae |

## Altar Tiers

| Tier | Notable unlocks |
| :---: | --- |
| **0** | Basic altar, Sacrificial Knife, Tabula Vitae, Hellfire Forge, Throwing Daggers |
| **I** | Upgrade Runes, sigil expansion, potioncrafting, off-hand orb harvesting |
| **II** | Rituals, Sentient Armor, Demon Realm peek (Breaching the Edge of Demon Realm) |
| **III** | Dusk Diviner, Sentient Evolution, Aspected Spiritus, permanent Endless Realm gate |
| **IV** | Most devastating rituals, Hellforged Parts double rune power |
| **V** | The pinnacle. Crystal Cluster capstones, nineteen runes per side, unlimited capacity |

See [Ara Vitae and Runes](Ara-Vitae-and-Runes) for construction details.

## Tools and Items

**Self-sacrifice**. Right-click your bound Blood Orb to bleed into it, then drop the orb in an altar to transfer the EV. An empty altar mid-craft also draws blood from nearby players to finish Tier 0 recipes.

**Sigil**. A one-handed tool bound to your Anima. Each costs a specific amount of EV per activation. See [Sigils](Sigils).

**Sigil of Holding**. Carries up to nine other sigils; cycle with a hotkey.

**Sentient Sword / Tools**. Drink Spiritus from kills, grow stronger.

**Sentient Armor**. Equipped with Upgrades and Downgrades. 100-point cap baseline; **Ritual of Sentient Evolution** raises it to 300.

**Throwing Dagger**. Early ranged weapon and a Spiritus harvester.

**Amethyst Throwing Dagger**. Throwing dagger that can be tipped with potion effects via the Tabula Vitae.

**Arcane Scribe Tool**. Strikes a circle on the ground; the starting Alchemy Array.

**Ritual Diviner**. Inscribes ritual stone layouts in-world. Upgraded variants (**Dusk Diviner**) unlock more powerful rites.

**Training Bracelet**. Curio that lets you train into specific Sentient Armor upgrades over time.

**Sigil of Ritual Diviner**. Rotates through known ritual layouts; color-codes required runes.

## Blocks

**Ara Vitae**. The central altar.

**Tabula Vitae**. Six-input alchemical bench.

**Hellfire Forge**. Spiritus-fueled crafting station.

**Athanor**. Material processor (raw -> fragment -> gravel -> dust -> ingot).


**Teleposer**. Linked-position spatial exchange. See [Teleposer](Teleposer).

**Master Ritual Stone**. Anchor for rituals. Inverted variant for ceiling/upside-down anchoring.

**Imperfect Ritual Stone**. Cheap, single-shot rituals.

**Blood Light**. Configurable colored light source. Brightness, hue, redstone sensitivity all tweakable.

**Blood Lantern**. A soul lantern reforged in the altar. Wards a 16-block radius against passive and ambient mob spawns. Skips suppression in the Dungeon dimension.

**Incense Altar**. Multi-block ritual aid for blessings.

**Blood Tank**. EV reservoir block.

**Blood Battery**. Energy storage block.

**Spirit Cache**. Container that stores special dungeon loot and emits light when it has contents.

**Spiritus Crystal**. Placeable crystal that grows by drawing Spiritus from the local field.

**Vas Maleficum**. The chunk Aura interface. Without redstone power, drains Spiritus from a held gem (or consumes a loose Spiritus item / Spiritus Crystal) into the surrounding chunk's Aura. With redstone power, reverses the flow and fills the gem from the Aura. See [Spiritus](Spiritus-Aspects-and-Crystals).

**Spira Infernalis**. Long-range Aura pylon. Each tick, pulls Spiritus from a chunk 16 blocks away in each cardinal direction toward its own chunk. Chain pylons to move Aura across distance.

**Crystallarium Maleficum**. Seeder block. When air is above and the chunk holds enough Aura (default 99), it spends that Aura over time to form the first **[Spiritus Crystal](Spiritus-Aspects-and-Crystals)** of the chunk's dominant Aspect on its top face. Further growth is then handled by the crystal itself.

**Spirit Accumulator**. Aura reservoir. Holds 1000 Spiritus of one Aspect. Placed unattuned; right-click to cycle the Aspect, crouch-click to lock it, or right-click with a Spiritus crystal to lock straight to that Aspect. Skims up to 25 per tick from its own chunk once locked, never below 30. Acts as a routing input node. See [Spiritus](Spiritus-Aspects-and-Crystals).

## Networks

**Routing Conduit**. Pipe for item/fluid/energy routing.

**Input Routing Node**, **Output Routing Node**. The endpoints of a routing network.

**Master Routing Node**. The brain of a routing network. Configures priorities, energy rates, filters.

**Spiritus channel**. Output Nodes carry a Spiritus tab: pick an Aspect and a Keep figure, and the node holds its own chunk's Aura at that level, drawing from Spirit Accumulators on the network. No adjacent block required.

See [Routing Network](Routing-Network).

## Dimension

**Dungeon dimension** (`neovitae:dungeon`). The Endless Realm. Procedurally assembled from hand-authored rooms.

**Breaching the Edge of Demon Realm**. Tier II ritual; one-shot Starter tier dungeon, limited passage to the Demon Realm.

**Highway to Hell**. Tier III ritual; permanent gate to the Endless tier dungeon.

## Defenses

**Sanguine Ward**. A shield raised with an Orb of Vitae in the off-hand. Stops ranged attacks from your front arc; melee still lands. Ordinary shots are consumed, while tridents and Throwing Daggers drop at the ward for retrieval. Costs EV per second while held. See [Blood Orbs and Anima](Blood-Orbs-and-Anima).

**Soul Snare**. Invisible status effect applied by Throwing Daggers to hostile mobs on hit. A snared mob, when killed, yields **Raw Spiritus** to its killer (this is how daggers harvest before a Sentient Sword is in hand). Does not affect mob behavior.

## Combat Modifiers

**Anointments**. Tool/weapon coatings applied without breaking your hand-hold. Deplete with use, not time. See [Tabula Vitae, Flasks and Anointments](Tabula-Vitae-Flasks-and-Anointments).

**Flask**. Reusable potion container with custom effect components and configurable duration tiers.

**Upgrade / Downgrade**. Sentient Armor modifiers competing for a configurable point budget. See [Sentient Armor](Sentient-Armor).

## In-Game Documentation

**Scriptura Vitae**. The in-game guidebook. Modonomicon-powered with parallax category backgrounds, custom recipe page types for every machine, and full prose explanations of every system. Craft one in vanilla crafting; it's the canonical player reference.
