# Ara Vitae and Runes

Every practitioner of Vitaemancy must begin here, at the **Ara Vitae**, the Altar of Life. This sacred basin transmutes raw blood into **Essentia Vitae** (EV), the refined lifeforce that fuels all vitaemantic works. It begins as a humble stone vessel, but as your mastery deepens, the Ara Vitae grows into a towering monument of rune-carved power.

## How the Altar Works

Once placed, the Ara Vitae awakens. It drinks the blood offered to it and distills it into Essentia Vitae, which it then uses to **transfigure** items laid within its basin. Right-click the altar to place an item upon it; right-click with an empty hand to retrieve it.

To feed the altar, you must first forge a **Sacrificial Knife**. Right-clicking while aiming at air spills one heart's worth of blood into a nearby Ara Vitae, yielding **200 EV**. The basin holds **10,000 EV** at first; watch the crimson pool within for a rough fill indicator, or use a **[Divination Sigil](Sigils)** to read exact figures.

The moment an item is placed upon the basin, the transmutation begins. EV drains steadily; crimson motes rising from the surface confirm the working is underway. Should the altar run dry mid-craft, gray smoke rises as the working unravels and progress is lost. EV cost is multiplied by stack size, so the basin must hold enough essence to finish the entire stack.

Your first creation should be the **Novicius Orb of Vitae**, a diamond offered to a Tier 0 altar with 2,000 EV. This orb is the key to your **[Anima](Blood-Orbs-and-Anima)**, the invisible network that binds your soul to all your vitaemantic instruments. Consult JEI for the full list of altar recipes.

> **Hidden reservoir:** 10% of the Ara Vitae's total capacity seeps into an internal vessel used for fluid transfer. If the numbers seem off, this unseen reservoir is the culprit.

## Tier Construction Summary

To ascend to greater tiers, you must inscribe **Blood Runes** and arrange them around the basin in precise patterns. Specialized runes confer different blessings; the most basic, the **Blank Rune**, carries no enchantment of its own and serves only as structural scaffolding.

| Tier | Total Runes | Notes |
|------|-------------|-------|
| 0    | 0           | A lone Ara Vitae, unadorned. |
| 1    | 8           | A ring around the basin. Cardinal slots accept specialized runes; corners stay inert until Tier 2. |
| 2    | 28          | 5 runes per edge, one level down and two blocks out. Stone pillars at each corner capped with **Blood Stained Glass**. |
| 3    | 56          | 7 runes per edge, again one level down and two further out. Four-block pillars crowned with **Bloodstone Bricks** (requires Tau Fruit from the *Breaching the Edge of Demon Realm* ritual). |
| 4    | 108         | 13 runes per edge, three blocks out, with a one-block gap at each end. **Hellforged Block** corners. |
| 5    | 184         | 19 runes per edge, three blocks beyond the last ring. No corner blocks at rune level; pillars ascend one tier higher and are crowned with **Crystal Clusters** (or Crystal Cluster Bricks). |

Crystal Clusters are forged in the **[Hellfire Forge](Hellfire-Forge-and-Sentient-Equipment)** from Sculk, an Tabula Aetherea, a Weak Blood Shard, and a Nether Star.

## Tabula

The Ara Vitae also forges **Tabula**, the inscribed stone tablets that serve as the foundation for nearly every vitaemantic creation. Each successive Tabula demands a more powerful altar and a steeper offering.

| Tabula            | Altar Tier | Cost       | Notes |
|------------------|------------|------------|-------|
| Tabula Rasa      | Tier 0     | 1,000 EV   | Smooth stone, first sigils etched. |
| Tabula Robur     | Tier 1     | 2,000 EV   | Sigils deepen; the stone hardens with purpose. |
| Tabula Animata   | Tier 2     | 5,000 EV   | The stone pulses with a faint, living warmth. |
| Tabula Spiritus  | Tier 3     | 15,000 EV  | Dark veins thread the tablet like frozen lightning. |
| Tabula Aetherea  | Tier 4     | 30,000 EV  | Almost translucent, hovering at the edge of the beyond. |

Two specialised variants exist for the alchemical bench: the **Tabula Vial** (a glass vessel reinforced with powdered Tabula, used to hold anointments) and the **Tabula Ampoule** (a small reservoir for crystallised EV, produced by certain throwing daggers and crushable for raw EV). Both are forged in the **[Tabula Vitae](Tabula-Vitae-Flasks-and-Anointments)** brewing rig.

## Rune Families

Runes fall into two grades: the standard inscription, and a **Reinforced** variant tempered with **Netherite Scrap** and **Intricate Hellforged Parts** plundered from the Demon Realm. Reinforced runes are roughly twice as potent (saving rune slots for other inscriptions), and the **[Athanor](Athanor-and-Materials)** can strip the reinforcement back to a base rune if needed.

### Throughput

| Rune            | Effect (per rune)                                                              |
|-----------------|-------------------------------------------------------------------------------|
| Speed           | +20% transmutation speed (additive). Idle progress also bleeds faster.        |
| Reinforced Speed| +40% transmutation speed (additive).                                          |
| Acceleration    | Removes 1 tick from the Charging/Displacement 20-tick pulse delay (min 1).    |
| Reinforced Acceleration | Removes 2 ticks from the pulse delay.                                 |

### Capacity

| Rune            | Effect (per rune)                                                              |
|-----------------|-------------------------------------------------------------------------------|
| Rune of Capacity | +20% to the basin's reservoir (additive).                                    |
| Reinforced Capacity | +40% reservoir (additive).                                                |
| Rune of Aug. Capacity | +7.5% reservoir (multiplicative, applied after additive Capacity Runes). |
| Reinforced Aug. Capacity | +15% reservoir (multiplicative).                                     |

### Charging

The Charging Rune siphons EV into a hidden internal reserve while the altar is idle. The moment an item is placed upon the basin, this stored charge floods into the transmutation at a perfect 1:1 ratio.

- The altar pulses once every 20 ticks (reduced by Acceleration Runes).
- Each pulse stores: **10 EV x Charging Runes x (1 + Speed Runes / 10)**.
- Maximum stored charge: **1,000 EV per Charging Rune**, scaled by *(altar capacity / 20,000)* if that ratio exceeds 1.
- Reinforced Charging Runes double both the storage rate and the maximum capacity.

### Sacrifice

| Rune            | Effect (per rune)                                                              |
|-----------------|-------------------------------------------------------------------------------|
| Rune of Sacrifice | +10% EV harvested from creatures slain near the altar (additive).            |
| Reinforced Sacrifice | +20% EV harvested (additive).                                              |
| Rune of Self-Sacrifice | +10% EV from your own wounds (Sacrificial Knife etc., additive).         |
| Reinforced Self-Sacrifice | +20% EV from your own wounds (additive).                              |

### Dislocation (Fluid Transfer)

| Rune            | Effect (per rune)                                                              |
|-----------------|-------------------------------------------------------------------------------|
| Displacement    | +20% fluid transfer rate (multiplicative). Essence flow between altar and external tanks. |
| Reinforced Displacement | +40% fluid transfer rate (multiplicative).                              |

### Orb

The Rune of the Orb resonates with the Blood Orb resting in the basin, stretching the bounds of your Anima while it sits there.

| Rune            | Effect (per rune)                                                              |
|-----------------|-------------------------------------------------------------------------------|
| Rune of the Orb | +2% to the Orb of Vitae's capacity (additive). Orb must remain in the basin.   |
| Reinforced Orb  | +4% to the orb's capacity (additive).                                         |

## Redstone and Automation

The Ara Vitae accepts mechanical servants. Items and EV alike can be piped in and out, with a few quirks.

- A simple **Hopper** feeds items into the basin, but knows no restraint; it will dump an entire stack and the altar will try to transmute all of it at once, multiplying the EV cost. If the basin can't keep up, the working stalls and progress bleeds away.
- The altar makes no distinction between input and output. Without a filter, items will cycle in and out as fast as your transfer system allows. **Routing Nodes** solve this neatly.
- The altar accepts EV transfers to and from external fluid tanks, but the flow is sluggish by default. **Acceleration Runes** quicken the altar's pulse; **Displacement Runes** widen the channel.
- The hidden 10% internal reservoir is the intermediary for piped fluid; essence may seem to vanish from the basin without explanation when that reservoir is filling or draining.

### Comparators

A comparator placed beside the Ara Vitae reads the basin level, much like a chest. Place a **Blood Stained Glass** block directly beneath the altar, and the comparator instead reads the **Anima** of whoever owns the orb in the basin. Signal strength scales to the orb's tier, not the maximum capacity of the network; 500,000 EV reads overflowing through a Novicius Orb but only half-strength through a Magus Orb. Clever Vitaemancers use this to shut down costly rituals before their Anima runs dry.

When the orb is bound to a team via NeoVitae Teams, the comparator reads the team's pooled Anima automatically. Placing a **Redstone Lamp** beneath the altar makes it emit a redstone pulse the instant a transmutation completes; useful for chained automation.

See also: **[Blood Orbs and Anima](Blood-Orbs-and-Anima)**, **[Sigils](Sigils)**, **[Rituals](Rituals)**, **[Hellfire Forge and Sentient Equipment](Hellfire-Forge-and-Sentient-Equipment)**.
