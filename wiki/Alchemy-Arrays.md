# Alchemy Arrays

Before sigils, before rituals, before the great workings of Vitaemancy were ever conceived, there was the **Alchemy Array**. Inscribed with the **Arcane Scribe Tool** upon bare stone or earth, the array is the most fundamental expression of power. It is a circle of intent, waiting to be given purpose.

## Inscribing an Array

Hold the Arcane Scribe Tool and press [Use] upon a solid surface. Each inscription consumes one charge; the tool holds **twenty uses** before wearing out. The empty array is inert. Click it with an item to place the **base** component; if the base is recognized, the design shifts to reflect what is being summoned.

A second item placed becomes the **catalyst**. If both are valid, the array awakens and its working begins. Both components are consumed as the array activates; nothing remains but the result of the working. Watch the pattern carefully when you place the base; if the glyph changes form, you are on the right path.

> Tip: the Arcane Scribe Tool can inscribe arrays directly into water source blocks, which makes the Undertow Array possible without draining a pool first.

Most **[Sigils](Sigils)**, reagents, and bindings begin their existence within a crafting array. 

## Dyeing the Array

The arcane lines are not bound to their natural color. Combine an **Arcane Scribe Tool** with any vanilla **dye** in a crafting grid (shapeless, any two slots) and the tool takes on the dye's hue. Inscriptions drawn with the tinted tool render in that color; the geometry is unchanged, only the pigment shifts.

The tint travels with the tool, not with the dye, so a single dyeing is enough for as many arrays as the scribe's remaining charges will allow. Combine the tool with a different dye to repaint it. The catalyst items consumed by the array work exactly the same regardless of color; this is a cosmetic affordance, not a recipe selector.

Already-inscribed arrays retain whatever color they were drawn with. To recolor an existing array, break it (it returns no items) and re-inscribe with a freshly tinted scribe.

## Crafting Arrays

Crafting Arrays are among the simplest expressions of the art. The array inscribes the essence of the base onto the catalyst, transmuting both into something new. A brief, elegant animation accompanies the transformation. Some recipes require only common materials; others demand reagents from the **[Tabula Vitae](Tabula-Vitae-Flasks-and-Anointments)** or beyond.

## Defensive Arrays

| Array | Effect |
|-------|--------|
| Spike Array | A cruel ward: any living creature that steps onto it suffers a full heart of damage. Nearly invisible once inscribed, ideal for defending passages or feeding automated arrangements. |
| Repulsion Array | An invisible ward that pushes hostile creatures away in a 5-block radius. Crafted from iron and lapis, accessible to apprentices. |
| Deflection Array | Projects a column of protective force (roughly 6 blocks tall, 3 wide) above it, reflecting any projectile that passes through back the way it came. Requires a Tabula Animata and a diamond. |
| Spirit Siphon Array | Combines Spike's cruelty with a deeper purpose: when a non-player creature steps onto it, the array deals a heart of damage and releases 0.5 units of raw **[Spiritus](Spiritus-Aspects-and-Crystals)** into the surrounding chunk. Has a brief cooldown; players are immune. Requires a Tabula Animata. |
| Vortex Sigil | A drawing array that pulls every living thing within **8 blocks** toward the block immediately below it, at roughly rocket-boosted-elytra speed. Upkeep costs ~0.2 EV/sec from the owner's anima (1% chance per tick to draw 1 EV); the pull pauses on any tick the network cannot pay. **Ignored entities:** creative/spectator players, any practitioner holding an **Orb of Vitae** (any tier) in main or off-hand, and anything inside the radius while a redstone signal is applied to the array. Inscribed from a Tabula Robur plus a **Blood Pearl** (ender pearl transmuted on a Tier 1 Ara Vitae). |

## Environmental Arrays

| Array | Effect |
|-------|--------|
| Tempest Array | Commands the weather, toggling rain on or off at a cost of **500 EV** from the owner's anima. Consumed on use. |
| Growth Array | Coaxes nearby crops and plants to grow faster in a 2-block radius, applying a gentle acceleration each second. |
| Freeze Array | Converts nearby water sources to ice and covers exposed solid ground in a thin layer of snow within a 3-block radius. Dissipates once everything in reach is frozen. |
| Undertow Array | Scribed with **Kelp** and awakened by **Redstone Dust**. Opens a vitaemantic current through the open column of air directly above the glyph, lifting or drowning any entity that enters. Right-click with an empty hand to reverse the flow; the array remembers its direction across world reloads. Strength scales with additional Redstone in the redstone slot (acceleration) and Kelp in the kelp slot (terminal velocity); right-click the array with more ingredients to stack them. |

## Movement and Transit Arrays

| Array | Effect |
|-------|--------|
| Speed Array | Launches entities horizontally in the direction they face. |
| Updraft Array | Hurls entities skyward with considerable force. |
| Teleposition Array | Folds space between two aligned glyphs for a seamless vertical elevator. Place at least two arrays directly above one another on the same block column, at any distance up to **64 blocks**. **Jump** while standing on an array to teleport upward to the next array above; **Sneak** to descend to the nearest below. Multiple arrays can be chained for multi-floor shafts. Each search is limited to the same X/Z column, so precise alignment matters. |

## Time Arrays

| Array | Effect |
|-------|--------|
| New Dawn (Day) | Advances the world to the next sunrise. Consumed on use. |
| True Twilight (Night) | Draws the world forward to the next sunset. Consumed on use. |

## Redstone Arrays

| Array | Effect |
|-------|--------|
| Signal Array | Outputs a redstone signal from 0 to 15 proportional to the EV stored in the owner's anima, scaling linearly up to 1,000,000 EV. Requires a bound Arcane Ash to function. |
| Trigger Array | Emits a brief (half-second) redstone pulse whenever a mob or player steps on it. Like a pressure plate, but nearly invisible. |

## Utility Arrays

| Array | Effect |
|-------|--------|
| Collection Array | Draws dropped items within 2 blocks toward its center. Place atop a chest and collected items deposit directly inside. |
| Light Array | Hangs 25 invisible full-strength lights in the open air one block above itself, filling a radius-3 diamond. A redstone signal takes them down; cutting the signal puts them back. Breaking the array removes them, unless you have fed it a block of Glowstone to make them permanent (see below). |
| Furnace Array | Transmutes raw materials dropped nearby into their smelted forms (10 EV per stack from the owner's network). Items within its radius will not despawn while awaiting processing; processes all valid stacks simultaneously, with cook times matching a standard furnace. |
| Endless Fountain Array | Scribed with a Block of Lapis and awakened by a Sea Pickle. Pipes water into every fluid container touching its six faces; every 5 ticks it deposits up to 6 buckets, spread evenly across neighbors. Only whole-bucket fills commit. The cache of adjacent tanks reacts instantly to neighbor changes, backs off progressively when every tank is full (with a slate-gray particle puff to signal the stall), and parks completely on a redstone signal. |

### The Light Array

The Light Array fills a **radius-3 diamond** (25 blocks) one block above itself with invisible light blocks, each at **full strength**. It only fills open air, so it will not push lights into your walls, and it re-checks the space every time it lights up.

- **Redstone.** A signal takes the lights down; cutting the signal puts them straight back. Handy for a room you want dark on demand.
- **Persistence.** Right-click the array with a **block of Glowstone** to fix the lights in place. The Glowstone is consumed, and from then on the lights stay when the array is broken, letting you light a build and take the array with you.
- **Clearing left-behind lights.** A Light Array adopts any light blocks already sitting in its diamond, so scribe a fresh one on the same spot and break it, and the old lights go with it. The same is true of a light block you placed by hand.

## Companions

| Array | Effect |
|-------|--------|
| Array of Loyal Friends | A restorative circle that summons all your tamed companions to your side. Living pets within range teleport in and are ordered to follow you; fallen pets are restored to full health at the array's location, though they lose any items they were carrying. Costs **5,000 EV** from your anima. The nearest player within 8 blocks is treated as the owner. Crafted with **Lead** as base, **Tabula Robur** as catalyst. |

## Sigil and Equipment Inscriptions

Many of the most important workings in the discipline are inscribed via Alchemy Arrays:

- All **[Sigils](Sigils)** (sigil arrays use a Reagent base + a Slate catalyst).
- The **[Training Bracelet](Sentient-Armor)** for controlling Sentient Armor learning.
- The **Ritual of Binding** that turns iron armor into **[Sentient Armor](Hellfire-Forge-and-Sentient-Equipment)** (Binding Reagent base + iron piece catalyst).

See also: **[Sigils](Sigils)** (where most arrays end), **[Tabula Vitae, Flasks and Anointments](Tabula-Vitae-Flasks-and-Anointments)** (where reagents are brewed), **[Sentient Armor](Sentient-Armor)** (the Ritual of Binding), **[Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals)** (Spirit Siphon Array).
