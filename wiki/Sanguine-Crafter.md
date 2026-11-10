# Sanguine Crafter

The **Sanguine Crafter** is a two-block-high 3x3 multiblock that crafts **crafting table**, **[Tabula Vitae](Tabula-Vitae-Flasks-and-Anointments)** and **[Hellfire Forge](Hellfire-Forge-and-Sentient-Equipment)** recipes on its own. Ingredients go into a buffer in any order, so hoppers, [Routing Nodes](Routing-Network), AE2 pattern providers and Refined Storage autocrafters can all keep it fed. Every recipe still pays what its own station would charge.

## Building It

The controller needs a **Tabula Spiritus**, so the crafter becomes available with a Tier 3 [Ara Vitae](Ara-Vitae-and-Runes).

| Block | Recipe |
| --- | --- |
| Sanguine Casing (x4) | 4 Bloodstone, 3 Iron Ingots, a Diamond and a Tabula Animata |
| Sanguine Crafter | A vanilla Crafter, a Tabula Spiritus and 7 Sanguine Casings |
| Hellforged Sanguine Casing | A Sanguine Casing and 4 Hellforged Ingots |
| Swift Sanguine Casing | A Sanguine Casing, 2 Tabula Animata and 2 Sugar |

Lay a 3x3 floor of casings and build a ring of eight on top of it. The **Sanguine Crafter** takes the middle of one side of the ring, facing outward. The block in the **center of the ring**, directly behind the crafter, can be anything: leave it empty or run a pipe or cable into it. Any casing position can hold a Hellforged or Swift casing instead. When the structure is complete, the casings join into one seamless shell. While it crafts, the crafter's face glows, sheds a little light and gives off a low hum. Whoever places the crafter owns it and pays its EV.

## The Menu

| Area | Purpose |
| --- | --- |
| Template grid (left, 3x3) | The recipe to craft. Click an item in, or press **+** on a recipe in JEI. Right-click a cell to clear it. |
| Output buffer (center, 3x3) | Finished items and leftovers such as empty buckets. |
| Orb and gem slots (right) | Fuel for Tabula Vitae and Hellfire Forge recipes. |
| Side buttons | Which faces reach the selected slot group (see [Automation](#automation)). |
| Mode, redstone and limit buttons | See below. |
| Status line | Why the crafter is waiting. Hover over it or the arrow for parallel crafts, speed and chunk loading. |
| Input buffer (18 slots) | Ingredients, in any order. |
| Chunk loading button (right of the input buffer) | Turns chunk loading on or off for this crafter. |

## Modes

| Mode | Crafts | Template |
| --- | --- | --- |
| **Auto** | The Tabula Vitae or Hellfire Forge recipe that exactly matches everything in the input buffer | Not used |
| **Crafting** | The template's crafting table recipe | Required |
| **Tabula** | The template's Tabula Vitae recipe | Required |
| **Forge** | The template's Hellfire Forge recipe | Required |

The template pulls the exact items shown in it from the buffer, so put the same items in the buffer that you put in the template. Pressing **+** in JEI sets both the template and the matching mode. Clicking an item into the template while in Auto switches the crafter to Crafting. Tabula Vitae flask recipes are not supported.

## Costs

| Recipe | Needs | Pays |
| --- | --- | --- |
| Crafting table | Nothing extra | 20 EV from the owner's network |
| Tabula Vitae | A bound Blood Orb of the recipe's tier in the orb slot | 20 EV from the owner, plus the recipe's EV from the orb's network |
| Hellfire Forge | A Spiritus Gem holding the recipe's minimum in the gem slot (or a gem among the ingredients, as in the forge) | 20 EV from the owner, plus the recipe's drain from the gem |

The gem in the gem slot refills from the chunk's Aura at the same rate as the Hellfire Forge's gem. A Cutting Fluid used as a Tabula Vitae ingredient loses a use instead of being consumed, as it does in the table.

## Upgrades

| Casing | Effect |
| --- | --- |
| Hellforged Sanguine Casing | +1 craft at a time, up to 16 |
| Swift Sanguine Casing | +25% crafting speed |

At the default base speed of 5x, a crafting table recipe takes 4 ticks, a Hellfire Forge recipe 20 ticks, and a Tabula Vitae recipe a fifth of its own crafting time. Swift casings speed it up from there, and a **[Ritual of Celerity](Rituals#automation-and-resources)** nearby doubles the result (ten times with Deus Celerity). When a craft takes less than a tick, the crafter finishes several in the same tick.

## Automation

Every casing and the crafter itself expose the crafter's inventory, so pipes and nodes can attach anywhere on the structure. A pipe in the center block reaches the casings around and below it; each casing sees it on the face that touches it, so the casing below the center counts it as its top face, the casing west of it as its east face, and so on. Slots are configured per face in four groups. Click an empty slot to select its group, then toggle the side buttons: green sides reach that group, red sides do not.

| Group | Default faces |
| --- | --- |
| Input buffer | Top, north, south, east, west |
| Output buffer | Bottom |
| Orb slot | None |
| Gem slot | None |

With the output buffer selected, **right-click** a side button to push results out of that side of the structure into whatever inventory is there. The pushing side gets a gold border. Right-click it again to stop. Press **Ctrl+C** in a machine's menu to copy all of its side settings, including the push side, then **Ctrl+V** in another machine of the same kind to paste them.

The **redstone** button switches between ignoring redstone, running only with a signal and running only without one; the signal is read at the crafter block. The **limit** pauses crafting while the output buffer holds that many of the result. Scroll over it to change it (Shift for 10, Ctrl for 64) and right-click it to remove the limit.

## AE2 and Refined Storage

The crafter works as an ordinary machine behind a pattern provider:

1. Use **Auto** mode, so one crafter handles every Tabula Vitae and Hellfire Forge pattern pushed into it.
2. Keep the orb and gem groups off the provider's face. AE2's blocking mode checks the machine through that face before each push.
3. Turn on **blocking mode** on the provider. Recipes only match their exact ingredients, so two patterns pushed at once would stall the crafter.
4. Push results out of the provider's face. An AE2 pattern provider takes them straight back into the network.

Crafting table patterns are better left to AE2 and Refined Storage, which assemble them themselves.

## Chunk Loading

While the structure is complete and running, the crafter keeps the chunks it stands in loaded, so crafting continues after you leave. By default this only happens while its owner is online, and each player can have up to 10 crafters loading chunks at once. Hover over the arrow to see whether a crafter is loading its chunks. To stop one crafter from loading chunks, click the Ender Eye button to the right of its input buffer; it turns into an Ender Pearl while chunk loading is off.

## Server Config

All options are in the `sanguine_crafter` section of the server config.

| Option | Default | Effect |
| --- | --- | --- |
| `ev_per_craft` | 20 | EV taken from the owner's network for every craft |
| `crafting_ticks` | 20 | Time of a crafting table recipe before speed multipliers |
| `forge_ticks` | 100 | Time of a Hellfire Forge recipe before speed multipliers |
| `base_speed` | 5.0 | Speed multiplier on every craft |
| `speed_per_swift_casing` | 0.25 | Speed added by each Swift Sanguine Casing |
| `max_parallel` | 16 | Most crafts one crafter performs at once |
| `chunk_loading` | true | Whether crafters keep their chunks loaded |
| `chunk_loading_owner_online_only` | true | Only load chunks while the owner is online |
| `chunk_loading_max_per_player` | 10 | Most chunk-loading crafters per player (0 for no limit) |

## Status Messages

| Status | Meaning |
| --- | --- |
| Structure incomplete | A casing is missing from the floor or the ring |
| Set a recipe template | A template mode is selected but the template is empty |
| No matching recipe | The template or buffer does not match a recipe of the selected kind |
| Missing ingredients | The buffer lacks an item the template needs |
| Needs a bound Blood Orb / Blood Orb tier too low | The Tabula Vitae recipe needs a better orb in the orb slot |
| Not enough EV | The owner's network, or the orb's, cannot pay |
| Gem lacks Spiritus | The gem holds less than the recipe's minimum |
| Output full / Output limit reached | Empty the output buffer, or raise the limit |
| Stopped by redstone | The redstone setting is holding it |
