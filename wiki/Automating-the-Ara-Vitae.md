# Automating the Ara Vitae

The [Ara Vitae](Ara-Vitae-and-Runes) exposes its crafting slot and its Essentia Vitae tank to adjacent blocks, so a [Routing Network](Routing-Network) can run an altar unattended. The basin holds **a single item at a time**, which is both the reagent you insert and the product it crafts in place. That makes it tidy to automate: feed one reagent, pull one product, repeat.

A full altar loop uses three faces.

**1. Feed the blood (optional).** Point a fluid **Output Node** at the altar from a full [Blood Tank](Ara-Vitae-and-Runes) and leave the fluid mode on **Auto-Match**. Essentia Vitae flows into the altar's tank and keeps it charged so crafting never stalls for want of blood. A Well of Suffering or manual sacrifice works too; the tank simply needs to stay full.

This step is genuinely optional. The altar's own basin stores enough Essentia Vitae to cover most crafts on its own, so for ordinary automation you can skip external blood entirely. Piping it in matters mainly if you run a **Dislocation / Acceleration rune** setup on the altar for large physical blood storage and want that reserve kept topped up.

**2. Feed the reagent.** Point an item **Output Node** at the altar from your reagent store. Set it to **Whitelist** and ghost the reagent (for a Tabula Rasa, that is Deepslate). The basin accepts only one item, so the node feeds a single reagent and waits for the slot to clear before sending the next; no keep amount or other fiddling is required.

**3. Pull the product.** Point an item **Input Node** at the altar into your output store. The tidiest filter is **Blacklist** with the reagent ghosted: everything that is *not* the raw reagent is pulled, which is precisely the finished product. While a craft is in progress the slot still holds the reagent, so the Blacklist leaves it untouched; the instant it becomes the product, the Input Node carries it away and the Output Node drips in the next reagent.

That is the entire loop: blood in, one reagent in, product out.

> The basin transmutes a single item per working, so an Output Node can never overfill it and a runaway craft can never drain your network dry. One reagent in, one product out, every time.

## With a Vitae Link

Routing the bare altar works, but it has two rough edges: the altar always climbs to its highest tier, and its single slot is both input and output, so pulling the product means a Blacklist trick. The [Vitae Link](Ara-Vitae-and-Runes#vitae-link) removes both.

A Link binds to a nearby altar and crafts on its behalf, **capped at a tier you choose** (one below the altar), with **separate input and output slots**. Drop a Link beside your altar instead of piping the altar directly:

1. **Feed the blood** to the altar exactly as above; the Link draws EV from the altar's basin, so the altar still needs to stay charged.
2. **Feed the reagent** with an item **Output Node** pointed at any side of the Link except the bottom. No filter trick is needed: the reagent only ever enters the input slot.
3. **Pull the product** with an item **Input Node** pointed at the **bottom** of the Link. Only finished product leaves the output slot, so a plain pull with no filter is enough.

Sneak + use the Link to set the tier you want (for example, cap at Tabula Robur instead of letting the altar run all the way to Aetherea). Drop several Links around one altar to craft different tiers in parallel from a single basin; the network feeds each one independently and the altar arbitrates so only one crafts at a time.

## AE2 / Refined Storage autocrafting

Because the Link presents a real input slot and a real output slot through standard item capabilities, it works as a **crafting target** for AE2 or Refined Storage, no Routing Network required.

- **Export** the reagent into the Link's input with an AE2 Export Bus / RS Exporter on any side except the bottom (or a Pattern Provider / Crafter pointed at it).
- **Import** the product from the bottom with an Import Bus / Importer (or let a Pattern Provider's returning items flow back).
- Build a **processing pattern** whose input is the reagent and whose output is the capped product. The Link caps the tier, so the pattern's output is deterministic; the altar supplies the EV. One Link per tier you want to autocraft, each set with sneak + use.

This lets a storage system request, say, "8 Tabula Animata" and have them assembled on demand from Deepslate, with the Link guaranteeing the craft stops at Animata instead of overshooting.

## With an Orb Vitae Link

Where the Vitae Link crafts, the **Orb Vitae Link** charges. Craft one from a Vitae Link, two iron ingots, and a piece of glass. It holds a single **Blood Orb** and binds to a nearby altar at the altar's **full tier and runes** (no tier cap). It pulls EV from the altar's basin into the bound owner's **Anima**, stopping once the network is full. It sits at the **bottom of the priority chain**: it only draws while the altar is idle and no Vitae Link is crafting, so it sips only the leftover essence and never starves an active craft.

1. **Seat your orb** by right-clicking the Link with a Blood Orb in hand; right-click empty-handed to take it back.
2. **Keep the altar charged** (orb deposits, an altar leech, or a Well of Suffering); the Link drains the basin to fill your Anima.
3. **Read the fill** off the Link's **comparator output**, which scales 0-15 with how full the owner's Anima is. Gate your blood generation with redstone, e.g. switch off a Well of Suffering once your network tops out.
4. **Pause it with redstone.** A redstone signal to the Link stops it charging for as long as it stays powered. A common setup wires the altar's own fill comparator (inverted) into the Link so it only draws once the basin is full, then pauses the instant the altar drops below full, letting the altar refill before the Link sips again.
