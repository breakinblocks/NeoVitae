# Routing Network

> *Hauling materials by hand is beneath a blood mage of your stature.*

The **Routing Network** is NeoVitae's logistics layer, moving items, fluids, Forge Energy, and Spiritus through invisible channels at your decree, sorted and filtered face by face. This page is more technical than the dungeon material; read it once, build a small test network, then come back for the details.

For spatial transport of *blocks themselves*, see [Teleposer](Teleposer). For running an [Ara Vitae](Ara-Vitae-and-Runes) unattended from the network, see [Automating the Ara Vitae](Automating-the-Ara-Vitae).

## The Three Components

A complete Routing Network has exactly three kinds of node, and a network needs all three to do work.

| Block | Role |
| --- | --- |
| **Master Routing Node** | The brain. One per network. Holds upgrades, energy throttle, and the graph itself. |
| **Input Routing Node** | Pulls items, fluids, or FE out of an adjacent inventory / tank / energy buffer. |
| **Output Routing Node** | Pushes items, fluids, or FE into an adjacent inventory / tank / energy buffer. |
| **Routing Conduit** | A passive relay that extends reach between active nodes. Pure plumbing. |

Every network requires **exactly one Master**. All other nodes must trace a path back to it, either directly or via conduits. Input and Output nodes interact with any **adjacent** block that supports item, fluid, or energy transfer; they have no internal storage of their own.

One further block joins a network without being a node block of its own: the **[Spirit Accumulator](Spiritus-Aspects-and-Crystals)** binds and links exactly like an Input Node, and offers whatever Spiritus it holds to the network. See [Spiritus](#spiritus) below.

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
- **Mode**. Toggles the filter behavior. See below.

The center of the UI is the **ghost grid**, a nine-wide field the currently-selected side uses to record what it permits or denies. When you fill the visible slots, a fresh **page** opens automatically; the **<** and **>** arrows above the grid page through them, so a single face can hold as many filters as you need. **Priority** buttons raise and lower the side's weight; **higher priority is served first** within the network.

### Items

With the **Items** tab selected, left-click a slot while holding an item to copy it into the ghost; right-click to clear. If JEI is installed, you can also drag an item straight from it onto a slot.

| Mode | Empty grid | Grid with ghosts |
| --- | --- | --- |
| **Whitelist** | Nothing passes | Only listed items pass |
| **Blacklist** | Everything passes | Everything except listed items passes |

### Fluids

The **Fluids** tab works the same way; left-click with a bucket or fluid vessel to copy its contents into the ghost, or drag a fluid from JEI onto a slot. Fluids support a third mode unavailable to items, **Auto-Match**, which mirrors whatever fluid is currently in the neighbor tank. This is the default for fresh sides, so an enabled face pointing at a [Blood Tank](Ara-Vitae-and-Runes) begins moving Essentia Vitae immediately.

### Keep Amounts

Each **Whitelist** ghost can carry an optional **keep amount**, shown in the corner of the slot. **Scroll** over the slot to raise or lower it; hold **Shift** for larger steps and **Ctrl** for larger still. **Zero means unlimited.** What the number does depends on the node's role:

- On an **Output** face it is a **target**. The node fills the destination until it holds that many of the item (or that many millibuckets of the fluid), counting what is already there, then stops. Use it to keep a machine topped up: keep 64 fuel in a furnace, keep 8000 mB of lava in a tank.
- On an **Input** face it is a **reserve**. The node leaves that many behind in the source and pulls only the surplus, so a buffer is never drained dry.

Keep amounts apply only to **Whitelist** ghosts; Blacklist and Auto-Match ignore them. A fresh ghost starts at zero (unlimited), and replacing a ghost resets its amount.

### Energy

Energy has **no whitelist or blacklist**. Any enabled face carries Forge Energy to compatible neighbors alongside items and fluids. The only gates are the face's **Enable** flag and the Master's **Energy Throttle**.

### Spiritus

The **Spiritus** tab is the odd one out: it has no ghost grid, no faces, and no adjacent block. It appears on **Output Nodes** only, and it stocks the node's **own chunk** with Spiritus drawn from [Spirit Accumulators](Spiritus-Aspects-and-Crystals) elsewhere on the network.

Two controls:

- **Type**. Cycles through **Off** and the five Aspects (Raw, Ruina, Nihilum, Vindicta, Invictus). Off disables the channel for this node.
- **Keep**. The amount of that Aspect to maintain in the node's chunk. Adjust in steps of **10**, or **100** with **Shift** held, up to **1000**. Zero disables the channel.

The node tops its chunk up toward the Keep figure and stops, exactly like a Keep Amount on an Output face, and it never pushes past the chunk's own Aura ceiling (**100** per Aspect by default), so a Keep figure larger than the ceiling simply fills the chunk. Face **Enable** flags and Priority work as usual; the Spiritus channel reads the **Up** face's priority.

Since no neighbor block is involved, an Output Node dedicated to Spiritus can sit alone in the chunk it feeds. Place one in each chunk that needs Aura and let the network keep them all supplied.

See [Automating Spiritus](Automating-Spiritus) for the full supply chain.

## The Master Routing Node

The Master is where you tune the network's behavior as a whole.

### Stack and Speed Upgrades

The Master has two upgrade slots, each accepting one upgrade type:

- **Routing Stack Upgrade** (stack slot, up to **64**) raises the **transfer ceiling** for all three resource types at once. A base operation moves **16 items**, **1,000 mB of fluid**, and **10,000 FE**; each upgrade adds another **16 items / 1,000 mB / 10,000 FE** on top.
- **Routing Speed Upgrade** (speed slot, up to **19**) shortens the **pulse interval**. The base rate is one operation every **20 ticks** (one second); each upgrade shaves off one tick, so 19 upgrades bring it down to one operation every tick.

Combine them according to taste and resource budget.

### Energy Throttle

Below the upgrade slots sits an **Energy (FE/t)** field. The value you type is the rate the network **requests** from the energy channel each pulse, defaulting to **500 FE/t**.

The true ceiling is set by the Routing Stack Upgrades installed above. Each upgrade raises the maximum FE/t the network can physically move; the field's label shows this ceiling. Type a figure higher than the ceiling and the ceiling prevails; the network moves the smaller of the two.

Why throttle at all? A modest setting spares your generators and prevents one network from collapsing a fragile power grid. Start low; raise once your sources can sustain it.

> The throttle only affects Forge Energy. Item and fluid transfer rates scale automatically with Routing Stack Upgrades.

## A Worked Example: Auto-Smelting Farm

Pull cobblestone from a generator, smelt it, drop smooth stone into storage.

1. **Master Routing Node** somewhere central, within auto-bind range of every worker node.
2. **Input Node** against the cobble generator's chest. Enable the face, items tab, **Whitelist**, drop cobblestone into a ghost slot.
3. **Output Node** against the input side of your furnaces. Enable, **Whitelist** + cobblestone ghost.
4. **Input Node** against the output side of the furnaces. Enable, **Whitelist** + smooth stone ghost.
5. **Output Node** against storage. Enable, **Blacklist** with empty grid (everything through; the input filter already enforced "only smooth stone").

If all four are within 16 blocks of the Master or a previously-linked node, they auto-bind. Look for four violet threads. If any miss, hit them with the Node Router.

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
