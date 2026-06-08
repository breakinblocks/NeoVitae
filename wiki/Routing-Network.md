# Routing Network

> *Hauling materials by hand is beneath a blood mage of your stature.*

The **Routing Network** is NeoVitae's logistics layer, moving items, fluids, and Forge Energy through invisible channels at your decree, sorted and filtered face by face. This page is more technical than the dungeon material; read it once, build a small test network, then come back for the details.

For spatial transport of *blocks themselves*, see [Teleposer](Teleposer).

## The Three Components

A complete Routing Network has exactly three kinds of node, and a network needs all three to do work.

| Block | Role |
| --- | --- |
| **Master Routing Node** | The brain. One per network. Holds upgrades, energy throttle, and the graph itself. |
| **Input Routing Node** | Pulls items, fluids, or FE out of an adjacent inventory / tank / energy buffer. |
| **Output Routing Node** | Pushes items, fluids, or FE into an adjacent inventory / tank / energy buffer. |
| **Routing Conduit** | A passive relay that extends reach between active nodes. Pure plumbing. |

Every network requires **exactly one Master**. All other nodes must trace a path back to it, either directly or via conduits. Input and Output nodes interact with any **adjacent** block that supports item, fluid, or energy transfer; they have no internal storage of their own.

## How Connections Happen

### Auto-Bind

Place an Input, Output, or Conduit within **16 blocks** of an existing network. It seeks a connection on its own, first the nearest **Master** in range; failing that, the closest already-bound node, inheriting that node's Master; failing both, it sits unbound. A successful auto-bind manifests as a brief **violet thread** arcing to the partner. No arc means no bind.

### The Node Router

When auto-bind is insufficient (long runs, dense layouts), the **Node Router** performs manual linking.

- **Shift-right-click a Master**: stores that Master's position.
- **Subsequent right-clicks on non-master nodes** within 16 blocks: binds each to the stored Master.
- **Shift-right-click a non-master node** instead: stores *that* position. Every subsequent right-click links them, and the stored position advances to the just-clicked node. Walk down a chain and the router links step by step.
- **Right-click empty air**: clears the stored position.

Each successful link emits a violet arcane bolt.

## Configuring a Node Face

When placed, an Input or Output Node detects every adjacent block, but it remains **dormant on every side** until configured. No external filter items are required; **each of the six faces carries its own filter** woven into the node itself. By default every side is disabled, and nothing flows.

Open the node's UI. The right column hosts directional buttons **D**own, **U**p, **N**orth, **S**outh, **W**est, **E**ast, with a block icon on sides facing inventories. The UI opens to the side facing an inventory by default (or Down if none does). Button orientation follows your facing direction; top is always "forward."

On the left column there are three controls:

- **Enable / Disable**. Wakes the selected side. A disabled face blocks *everything* regardless of filter configuration.
- **Items / Fluids tab**. Switches the ghost grid between item and fluid filtering.
- **Mode**. Toggles the filter behaviour. See below.

The center of the UI is the **ghost grid**, a nine-wide field the currently-selected side uses to record what it permits or denies. When you fill the visible slots, a fresh **page** opens automatically; the **<** and **>** arrows above the grid page through them, so a single face can hold as many filters as you need. **Priority** buttons raise and lower the side's weight; **higher priority is served first** within the network.

### Items

With the **Items** tab selected, left-click a slot while holding an item to copy it into the ghost; right-click to clear. If JEI is installed, you can also drag an item straight from it onto a slot.

| Mode | Empty grid | Grid with ghosts |
| --- | --- | --- |
| **Whitelist** | Nothing passes | Only listed items pass |
| **Blacklist** | Everything passes | Everything except listed items passes |

### Fluids

The **Fluids** tab works the same way; left-click with a bucket or fluid vessel to copy its contents into the ghost, or drag a fluid from JEI onto a slot. Fluids support a third mode unavailable to items, **Auto-Match**, which mirrors whatever fluid is currently in the neighbour tank. This is the default for fresh sides, so an enabled face pointing at a [Blood Tank](Ara-Vitae-and-Runes) begins moving Essentia Vitae immediately.

### Keep Amounts

Each **Whitelist** ghost can carry an optional **keep amount**, shown in the corner of the slot. **Scroll** over the slot to raise or lower it; hold **Shift** for larger steps and **Ctrl** for larger still. **Zero means unlimited.** What the number does depends on the node's role:

- On an **Output** face it is a **target**. The node fills the destination until it holds that many of the item (or that many millibuckets of the fluid), counting what is already there, then stops. Use it to keep a machine topped up: keep 64 fuel in a furnace, keep 8000 mB of lava in a tank.
- On an **Input** face it is a **reserve**. The node leaves that many behind in the source and pulls only the surplus, so a buffer is never drained dry.

Keep amounts apply only to **Whitelist** ghosts; Blacklist and Auto-Match ignore them. A fresh ghost starts at zero (unlimited), and replacing a ghost resets its amount.

### Energy

Energy has **no whitelist or blacklist**. Any enabled face carries Forge Energy to compatible neighbours alongside items and fluids. The only gates are the face's **Enable** flag and the Master's **Energy Throttle**.

## The Master Routing Node

The Master is where you tune the network's behaviour as a whole.

### Stack and Speed Upgrades

Stack upgrades raise the ceiling on **transfer rate** for all three resource types simultaneously, governing items per pulse, mB of fluid per pulse, and FE/t. Speed upgrades shorten the pulse interval. Combine them according to taste and resource budget.

### Energy Throttle

Below the upgrade slots sits an **Energy (FE/t)** field. The value you type is the rate the network **requests** from the energy channel each pulse, defaulting to **500 FE/t**.

The true ceiling is set by the Stack Upgrades installed above. Each upgrade raises the maximum FE/t the network can physically move; the field's label shows this ceiling. Type a figure higher than the ceiling and the ceiling prevails; the network moves the smaller of the two.

Why throttle at all? A modest setting spares your generators and prevents one network from collapsing a fragile power grid. Start low; raise once your sources can sustain it.

> The throttle only affects Forge Energy. Item and fluid transfer rates scale automatically with Stack Upgrades.

## A Worked Example: Auto-Smelting Farm

Pull cobblestone from a generator, smelt it, drop smooth stone into storage.

1. **Master Routing Node** somewhere central, within auto-bind range of every worker node.
2. **Input Node** against the cobble generator's chest. Enable the face, items tab, **Whitelist**, drop cobblestone into a ghost slot.
3. **Output Node** against the input side of your furnaces. Enable, **Whitelist** + cobblestone ghost.
4. **Input Node** against the output side of the furnaces. Enable, **Whitelist** + smooth stone ghost.
5. **Output Node** against storage. Enable, **Blacklist** with empty grid (everything through; the input filter already enforced "only smooth stone").

If all four are within 16 blocks of the Master or a previously-linked node, they auto-bind. Look for four violet threads. If any miss, hit them with the Node Router.

## Automating the Ara Vitae

The [Ara Vitae](Ara-Vitae-and-Runes) exposes its crafting slot and its Essentia Vitae tank to adjacent blocks, so a Routing Network can run an altar unattended. The basin holds **a single item at a time**, which is both the reagent you insert and the product it crafts in place. That makes it tidy to automate: feed one reagent, pull one product, repeat.

A full altar loop uses three faces.

**1. Feed the blood.** Point a fluid **Output Node** at the altar from a full [Blood Tank](Ara-Vitae-and-Runes) and leave the fluid mode on **Auto-Match**. Essentia Vitae flows into the altar's tank and keeps it charged so crafting never stalls for want of blood. A Well of Suffering or manual sacrifice works too; the tank simply needs to stay full.

**2. Feed the reagent.** Point an item **Output Node** at the altar from your reagent store. Set it to **Whitelist** and ghost the reagent (for a Tabula Rasa, that is Deepslate). The basin accepts only one item, so the node feeds a single reagent and waits for the slot to clear before sending the next; no keep amount or other fiddling is required.

**3. Pull the product.** Point an item **Input Node** at the altar into your output store. The tidiest filter is **Blacklist** with the reagent ghosted: everything that is *not* the raw reagent is pulled, which is precisely the finished product. While a craft is in progress the slot still holds the reagent, so the Blacklist leaves it untouched; the instant it becomes the product, the Input Node carries it away and the Output Node drips in the next reagent.

That is the entire loop: blood in, one reagent in, product out.

> The basin transmutes a single item per working, so an Output Node can never overfill it and a runaway craft can never drain your network dry. One reagent in, one product out, every time.

### With a Vitae Link

Routing the bare altar works, but it has two rough edges: the altar always climbs to its highest tier, and its single slot is both input and output, so pulling the product means a Blacklist trick. The [Vitae Link](Ara-Vitae-and-Runes#vitae-link) removes both.

A Link binds to a nearby altar and crafts on its behalf, **capped at a tier you choose** (one below the altar), with **separate input and output slots**. Drop a Link beside your altar instead of piping the altar directly:

1. **Feed the blood** to the altar exactly as above; the Link draws EV from the altar's basin, so the altar still needs to stay charged.
2. **Feed the reagent** with an item **Output Node** pointed at any side of the Link except the bottom. No filter trick is needed: the reagent only ever enters the input slot.
3. **Pull the product** with an item **Input Node** pointed at the **bottom** of the Link. Only finished product leaves the output slot, so a plain pull with no filter is enough.

Sneak + use the Link to set the tier you want (for example, cap at Tabula Robur instead of letting the altar run all the way to Aetherea). Drop several Links around one altar to craft different tiers in parallel from a single basin; the network feeds each one independently and the altar arbitrates so only one crafts at a time.

### AE2 / Refined Storage autocrafting

Because the Link presents a real input slot and a real output slot through standard item capabilities, it works as a **crafting target** for AE2 or Refined Storage, no Routing Network required.

- **Export** the reagent into the Link's input with an AE2 Export Bus / RS Exporter on any side except the bottom (or a Pattern Provider / Crafter pointed at it).
- **Import** the product from the bottom with an Import Bus / Importer (or let a Pattern Provider's returning items flow back).
- Build a **processing pattern** whose input is the reagent and whose output is the capped product. The Link caps the tier, so the pattern's output is deterministic; the altar supplies the EV. One Link per tier you want to autocraft, each set with sneak + use.

This lets a storage system request, say, "8 Tabula Animata" and have them assembled on demand from Deepslate, with the Link guaranteeing the craft stops at Animata instead of overshooting.

### With an Orb Vitae Link

Where the Vitae Link crafts, the **Orb Vitae Link** charges. Craft one from a Vitae Link, two iron ingots, and a piece of glass. It holds a single **Blood Orb** and binds to a nearby altar at the altar's **full tier and runes** (no tier cap). It pulls EV from the altar's basin into the bound owner's **Anima**, stopping once the network is full. It sits at the **bottom of the priority chain**: it only draws while the altar is idle and no Vitae Link is crafting, so it sips only the leftover essence and never starves an active craft.

1. **Seat your orb** by right-clicking the Link with a Blood Orb in hand; right-click empty-handed to take it back.
2. **Keep the altar charged** (orb deposits, an altar leech, or a Well of Suffering); the Link drains the basin to fill your Anima.
3. **Read the fill** off the Link's **comparator output**, which scales 0-15 with how full the owner's Anima is. Gate your blood generation with redstone, e.g. switch off a Well of Suffering once your network tops out.
4. **Pause it with redstone.** A redstone signal to the Link stops it charging for as long as it stays powered. A common setup wires the altar's own fill comparator (inverted) into the Link so it only draws once the basin is full, then pauses the instant the altar drops below full, letting the altar refill before the Link sips again.

## Topology and Reach

### Chained Conduits

A single auto-bind is limited to 16 blocks, but a **chain** of conduits can extend arbitrarily far. Each new conduit auto-binds to the previous one, inheriting the same Master.

### Unloaded Reach

The Master remembers the full shape of its network, and the **Routing Conduits threading it together do not need to stay in loaded chunks for the network to function**. So long as the Master and the Input/Output nodes you care about are loaded, the conduits between them may rest in unloaded space. Break a conduit mid-network while you are present and the Master forgets it instantly; everything downstream recalculates.

### Self-Mending and Rescan

Once per minute the Master sweeps loaded portions of its graph, scrubbing positions that no longer hold a routing-node block. Individual nodes stamp their own coordinates into save data; if a node awakens at a different location it discards its old allegiance and seeks a new Master.

For a truly ruined network, an administrator may invoke `/neovitae routing rescan` while looking at or standing near the afflicted Master. The Master abandons every remembered connection and walks nearby loaded chunks, rebuilding its graph from scratch. Use it after save-file surgery, chunk regeneration, or world-edit catastrophes.

## Practical Tips

- **Start every face disabled** and turn them on one at a time. The most common bug is a forgotten Enable flag.
- **Use priorities sparingly.** Raise priorities only for known-critical inputs and outputs.
- **Throttle FE before you trust the network.** 500 FE/t default is conservative; raise once generation matches.
- **Fluid Auto-Match is the right default** for a face pointing at a single-fluid tank. Switch to Whitelist/Blacklist only when triaging multiple liquids.
- For block-level transport (spawners, structures, vertical elevators), the **[Teleposer](Teleposer)** is the correct tool, not the Routing Network.
