# Athanor and Materials

The **Athanor** is the alchemist's furnace, a vessel of transmutation that transcends mere smelting. Within its core you may quintuple ore yields, revert Orbs of Vitae, unmake Netherite and Reinforced Runes, and most critically distill **Weak Blood Shards** from Saturated Tau.

> Most tools placed in the Athanor degrade over time, but Unbreaking or Mending enchantments will slow or halt their decay.

## Operating the Athanor

The Athanor is sided, much like a common furnace. **Tools** may only enter or exit from the **top**. **Inputs** are accepted from the sides. **Outputs** emerge from the bottom. Keep this geometry in mind when placing your Hoppers or Routing Nodes; an automated Athanor is the heart of any serious ore-processing chain.

It accepts only two fuel sources, the **Primitive Fuel Cell** (a Block of Coal worked in a recipe) or a **Lava Crystal**. The Primitive Fuel Cell endures for **128 individual operations**, more than double what its Block of Coal would yield in a furnace, and wears down only on completing a craft so nothing is wasted. The Lava Crystal is a shard of imprisoned heat born from molten rock and sustained by your **[Anima](Blood-Orbs-and-Anima)**; placed in any furnace it becomes an inexhaustible flame, drawing **50 EV** per item smelted (ten seconds per operation, one item per cycle).

### Spiritus Sensitivity

The Athanor is sensitive to the **Raw [Spiritus](Spiritus-Aspects-and-Crystals)** saturating the surrounding chunk. With no spiritus present, the vessel operates at **half speed**; as raw spiritus builds toward 100, the Athanor accelerates to **double speed** at full saturation. While crafting, there is a **5% chance per second** that the Athanor consumes 1 raw spiritus from the chunk; keep Spiritus Crystals growing nearby to replenish what it consumes.

Some advanced recipes require a specific amount of spiritus in the chunk to craft. The Athanor's interface shows a gauge with current spiritus levels and a red overlay marking how much is needed. If the chunk lacks sufficient spiritus, the Athanor stalls and emits dark particles until the deficit is replenished. Spiritus costs are consumed on craft completion, not during progress.

### The Sanguine Reverter

Forged in the Hellfire Forge, the **Sanguine Reverter** is a tool of unmaking. It peels away the enchantments of creation, reducing Orbs of Vitae, Netherite, and Reinforced Runes back to their constituent materials. It is also the instrument by which **Weak Blood Shards** are born from **Saturated Tau**. Reversion targets include:

- Novicius, Discipulus, Veneficus, and Magus Orbs of Vitae
- Netherite Ingot
- Any Reinforced Rune (back to base materials, invaluable when restructuring your altar)

## Ore Processing: From Vein to Ingot

You have two paths and you choose by tempo. The **[Tabula Vitae](Tabula-Vitae-Flasks-and-Anointments)** doubles your ore in a single quick step. The **Athanor** is slower but far more rewarding: a unified refinement chain yields **5 ingots per silk-touched ore block, 3 per raw ore** at the floor, and rises further when **[Raw Spiritus](Spiritus-Aspects-and-Crystals)** soaks the chunk.

### Cutting Fluids

Cutting Fluid is the universal reagent for every Athanor ore-processing step, prepared in the **[Tabula Vitae](Tabula-Vitae-Flasks-and-Anointments)**. Three grades exist:

| Fluid | Effect |
|-------|--------|
| Basic Cutting Fluid | The baseline. A Bottle of Water may substitute for the Water Sigil in the recipe. |
| Intermediate Cutting Fluid | Endures 8x as long, hastens the Athanor's work by 50%. Requires **Tau Oil** from the Demon Realm. |
| Advanced Cutting Fluid | Persists 16x longer, doubles crafting speed, and doubles bonus-yield probability. Requires **Hellforged Dust** from the deepest Demon Realm. |

### The Refinement Chain

A single path from rock to ingot, every step driven by Cutting Fluid except the resonator stage:

1. **Ore block + Cutting Fluid** = 5 **Ore Fragments**.
2. **Raw ore + Cutting Fluid** = 3 **Ore Fragments**.
3. **Each fragment + Resonator** = 1 **Ore Gravel**, with a 50% chance of a **Tiny Corrupted Dust** byproduct.
4. **Each gravel + Cutting Fluid** = 1 **Metal Dust**.
5. **Each dust smelted** in any furnace = 1 ingot.

The **Resonator** is forged in the **[Hellfire Forge](Hellfire-Forge-and-Sentient-Equipment)**. **Reinforced** and **Hellforged** Resonator variants are far more durable; the Hellforged variant also doubles the Corrupted Dust byproduct rate.

Tiny Corrupted Dust combines into full **Corrupted Dust**, a potent catalyst. Corrupted Dust merges with various materials in the Tabula Vitae to produce **Corrupted Coal**, **Corrupted Iron**, and **Corrupted Debris**.

### The Spiritus Boost

When **Raw [Spiritus](Spiritus-Aspects-and-Crystals)** saturates the chunk in which the Athanor sits, every ore-to-fragment craft gains a chance to yield one extra fragment. The chance scales with chunk saturation:

| Raw Spiritus in chunk | Bonus chance |
|---|---|
| Below 5 | 0% (no bonus) |
| 5 | 33% |
| 50 (midpoint) | ~67% |
| 100 or higher | 100% (always +1) |

Linear scaling between the thresholds. Each successful bonus has a small **2.5% chance** to consume **1 Raw Spiritus** from the chunk, so heavily saturated chunks deplete slowly under heavy industry. Pair an Athanor with a **Spiritus Crystal** array to keep the well topped up.

At full saturation the Athanor effectively yields **6 ingots per silk-touched ore block** or **4 per raw ore** before any further bonus from advanced cutting fluid.

### Yield Summary

| Input | Floor (no spiritus) | Cap (100+ Raw Spiritus) |
|---|---|---|
| Ore block (Silk Touch) | **5 ingots** | **6 ingots** |
| Raw ore | **3 ingots** | **4 ingots** |
| Ore block via Tabula Vitae | 2 ingots | 2 ingots (no scaling) |
| Ore block via vanilla furnace | 1 ingot | 1 ingot |

### The Hydration Cell

Fitted with a **Primitive Hydration Cell**, the Athanor becomes a font of elemental water. Supply it with Water, or the cell thirsts in vain. Among its uses:

- **Saturated Tau -> Weak Blood Shard** (requires Essentia Vitae), the keystone production chain for Tier 3 altar capstones.
- **Sand -> Clay** and **Terracotta -> Clay**.
- **Dirt -> Mud**.
- **Concrete Powder -> Concrete** for all sixteen hues.
- Strips dye from beds, wool, carpets, and glass (any colour to white/clear).
- Cultivates moss on cobblestone, stone bricks, and their stair/slab/wall variants.
- Hastens copper oxidation through every stage (block, cut, stairs, slabs).

## Bloodstone Bricks

Bloodstone Bricks are stone saturated with crystallised life force; dark and warm to the touch. Beyond their unsettling beauty, they serve as the **capstones for the Tier 3 Ara Vitae**. Their creation requires **Weak Blood Shards**, obtained by placing Saturated Tau in the Athanor with the Sanguine Reverter, and the Saturated Tau itself comes from the Demon Realm. See **[Ara Vitae and Runes](Ara-Vitae-and-Runes)** for the tier-construction chart.

## Bucket of Essentia Vitae

Essentia Vitae is potent within the Ara Vitae, but it can also be poured into the world. Place an empty **Bucket** in the **[Ara Vitae](Ara-Vitae-and-Runes)** while it holds at least 1,000 EV and the altar fills the vessel in moments. One unit of EV equals one millibucket. Useful for filling a moat around your Incense Altar, or for any working that calls for liquid lifeforce. It is emphatically not blood; blood would have coagulated long ago.

See also: **[Ara Vitae and Runes](Ara-Vitae-and-Runes)** (the altar that consumes the materials), **[Hellfire Forge and Sentient Equipment](Hellfire-Forge-and-Sentient-Equipment)** (forges the Sanguine Reverter and Resonators), **[Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals)** (the chunk-aura the Athanor draws on), **[Tabula Vitae, Flasks and Anointments](Tabula-Vitae-Flasks-and-Anointments)** (where Cutting Fluid is brewed).
