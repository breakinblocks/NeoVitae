# Spiritus, Aspects and Crystals

Every creature that walks, crawls, or slithers through the dark carries within it a shard of demonic intent, a residue left when entities of the lower planes imbue their malice into mortal flesh. This essence is known as **Spiritus**, and it is yours to harvest. Where Essentia Vitae is the currency of the blood, Spiritus is the currency of the soul.

## Harvesting Spiritus

Two methods exist:

- Strike a hostile creature with a **[Throwing Dagger](Hellfire-Forge-and-Sentient-Equipment)**; the wound binds spectral motes to its form, and slaying it while marked yields its Spiritus.
- Fell it outright with a **Sentient Sword**.

As a fledgling Vitaemancer, you will not yet possess a Sentient Sword, so the Throwing Dagger shall serve as your first instrument of collection. Loose Spiritus rattling around your pack is unwieldy; transfer it into a **Spiritus Gem** for safekeeping.

## Spiritus Gems

The gem is a crystalline prison that contains and compresses Spiritus into something manageable. Better still, the gem hungers; drop loose Will upon the ground nearby and the gem will devour it of its own accord. To transfer Will between gems, right-click while holding the gem you wish to empty; its contents flow into the first valid gem in your inventory.

| Gem | Storage | Notes |
|-----|---------|-------|
| Petty | 64 | The first vessel. Crude but practical. |
| Lesser | 256 | Reinforced with Diamond, Lapis, and Redstone in the Forge. |
| Common | 1,024 | Requires an Tabula Animata, another Diamond, and a Block of Gold. |
| Greater | 4,096 | A masterwork. Demands a Tabula Spiritus, a Weak Blood Shard, and a Spiritus Crystal. |
| Grand | 16,384 | The pinnacle of containment. |

When upgrading, the Forge draws Spiritus from the gem being crafted before tapping the gem in its Gem Slot. The newly forged gem retains any leftover Spiritus.

A charged gem can also pocket a spawner. Sneak + right-click a Monster Spawner or Trial Spawner with a gem holding at least 200 Spiritus (configurable via `spawner_capture_cost` in the server config); that Spiritus is spent and the spawner drops as an item carrying its bound creature and settings, ready to be placed elsewhere.

## The Spiritus Aura

Demonic Spiritus can also exist in a diffused state throughout the very air. By burning Spiritus in a **Vas Maleficum**, you release it into the **Aura**, an invisible miasma that permeates each chunk of the world.

Feed the Vas Maleficum a charged Spiritus Gem, loose Spiritus, or Spiritus Crystals of any Aspect. Crystals are consumed once the chunk's Spiritus dips below 50; loose Spiritus and gem contents are consumed gradually as needed.

Apply a redstone signal and the Vas reverses direction: a Spiritus Gem placed inside is **filled** from the chunk's Aura instead of drained. The same vessel serves as both deposit and withdrawal point.

The Aura is **chunk-based**. Spiritus burned in one chunk fills only that chunk, up to a cap of **100 per aspect**. Measure local concentration with a **Spiritus Aura Gauge** (see below). To move Spiritus across distances, place a **Spira Infernalis**: each tick it probes a position **16 blocks away** in each of the four cardinal directions, and if the probed position holds more Spiritus than the pylon's own chunk, the pylon pulls a small fraction of the difference. The flow equilibrates rather than draining the source dry, so chain pylons from a saturated chunk toward your worksite to ferry Aura over long distances.

This reversal is how an aspected gem is made. Saturate a chunk with a single Aspect, leave an empty Spiritus Gem in a redstone-charged Vas Maleficum there, and the gem takes on that Aspect as it fills.

### Aura Gauge

While the **Spiritus Aura Gauge** rests in your inventory, it projects a spectral reading onto your vision. From top to bottom the bars measure Raw, Ruina, Invictus, Nihilum, and Vindicta. Hold sneak for a precise numerical reading (0 to 100 per Aspect).

## Crystallized Spiritus

The **Crystallarium Maleficum** seeds the first spire of a cluster: place it with open air above, and once the chunk's Aura holds at least **99 Spiritus** of any single Aspect, it spends that Aura over roughly 1,000 ticks to form a single **Spiritus Crystal** of the chunk's dominant Aspect on its top face. After that first spire stands, the crystal grows under its own power; the Crystallarium has done its work.

Spires grow whenever the chunk holds at least a trace of the cluster's Aspect, with growth rate scaling against saturation, accelerating as the Aura fills. A chunk's natural cap is **100 per Aspect**, though certain Rituals can raise it. Each new spire costs **45 Spiritus** when the chunk's dominant Aspect matches the cluster, and yields 50 when burned in a Vas Maleficum (a net gain of 5 per spire). If the chunk's dominant Aspect **does not match** the cluster (for instance, a Raw-dominant chunk feeding a Ruina cluster), the cost rises to **90 Spiritus** per spire and growth runs at **60% speed**. Keep the chunk biased toward the Aspect you want to farm. A cluster may grow up to **7 spires** tall.

If you carry more than **512 total Spiritus** in your inventory (across any gems, any type), you may harvest clusters by right-clicking with an empty hand. This strips all but the central spire, leaving the cluster to regrow. A pickaxe will shatter the entire cluster at once for the impatient. Manual harvest yields a modest 5 Spiritus per cluster.

## The Five Aspects

Raw clusters feel conflicted, as though warring natures strain against one another within. With the right catalyst, those hidden facets can be coaxed into purer forms.

- **Raw Spiritus**. Undifferentiated malice.
- **Spiritus Invictus**. The unbroken; a fortress of intent that refuses to be undone.
- **Spiritus Nihilum**. The final silence; weight without haste, an ending given form.
- **Spiritus Vindicta**. The swift reckoning; vengeance taken before the offense is voiced.
- **Spiritus Ruina**. The slow undoing; patient decay that wears all things to ruin.

Each Aspect can be burned in the Vas Maleficum just as Raw Spiritus can, feeding the Aura with its particular resonance for your Rituals to draw upon.

### Aspect Behavior on Sentient Tools

Each Aspect also transforms the behavior of **Sentient Tools** and the **Sentient Sword**:

| Aspect | Effect on Sentient Equipment |
|--------|-----------------------------|
| Raw | Pure damage increase. |
| Ruina | Strikes may inflict poison or wither. Otherwise identical to Raw. |
| Vindicta | Moderate damage increase, heightened attack speed, and a movement speed boon that intensifies with greater Spiritus reserves. |
| Invictus | Moderate damage increase, and slaying a foe grants you a protective shield of Absorption. |
| Nihilum | The greatest raw damage of any Aspect, at the cost of reduced attack speed. |

To fill a gem with a specific Aspect, feed a Vas Maleficum Spiritus Crystals of that Aspect until the chunk's Aura is saturated, then place the empty gem in that same Vas and apply a redstone signal.

A gem holds only one Aspect at a time, and an empty gem takes whichever Aspect the Aura offers first. Where several are present they are taken in a fixed precedence: Raw first, then Ruina, Nihilum, Invictus, and Vindicta last. Any trace of Raw in the chunk will therefore claim an empty gem before any other Aspect is considered, so dedicate a separate chunk to each Aspect you intend to bottle.

Right-click while holding a Sentient Tool to recalibrate it to the dominant Aspect in your inventory. Your tools attune to whichever Aspect you carry in greatest quantity, so ten Ruina in one gem and a thousand Raw in another means the blade remains Raw.

The Hellfire Forge itself accepts any Aspect for crafting, so there is no need to juggle multiple gems between stations.

## Spiritus Catalysts

Five catalysts exist, one for each Aspect (Raw, Ruina, Nihilum, Vindicta, Invictus). Each is forged in the Hellfire Forge and serves two purposes.

### Bootstrapping Aspected Crystals

A Crystallarium Maleficum will only grow Raw clusters until the chunk's Aura is dominated by a different Aspect, but that demands aspected crystals you do not yet possess. The catalyst breaks the cycle. Right-click a fully-grown **Raw** cluster with an aspected catalyst (Ruina, Nihilum, Vindicta, or Invictus) and the cluster transmutes into the catalyst's Aspect, restarting at age zero. The catalyst is consumed.

Transmutation also demands one **Animus Mote** be present in your inventory. The mote splinters the cluster's natures into a single focused Aspect. Animus Motes drop from **Daemonium Animaris** in Standard Dungeons, and appear in `great_loot` and `decent_loot` Standard Dungeon chests. Four motes are enough to seed every Aspect lineage; thereafter you only need them for new farms.

Once you have one aspected cluster of a given Aspect, burn its harvested shards in a Vas Maleficum to make that Aspect dominant in the chunk. The Crystallarium will then form new clusters of that Aspect natively, no further motes required.

### Accelerating Existing Clusters

Right-click a cluster with a **matching-Aspect** catalyst (e.g., Ruina catalyst on a Ruina cluster) and the cluster's growth is supercharged; per-spire Spiritus cost drops from 45 to **25**, and growth accelerates tenfold. One dose fuels ten spires, netting **+200 Spiritus per catalyst** versus passive aura growth. A second dose extends the effect to twenty growths total. Same-aspect acceleration does **not** consume an Animus Mote.

### Automation Loop

Pair the catalyst with the **Crystallum Fractura** ritual (see **[Rituals](Rituals)**) for a hands-off farm; the ritual auto-harvests fully-grown clusters, doubles crystal growth speed, and amplifies any Spiritus injection by +25% across the chunks in range. With the Master Ritual Stone attuned via the Ritual Configurator (carry exactly one aspect crystal in your hotbar in SET_WILL_CONFIG mode), the +25% injection bonus is biased toward any Aspect you choose. A modest **Routing Node** arrangement can then ship harvested shards back into a Vas Maleficum to maintain saturation, closing the loop.

## Spiritus Infusion

Through the Hellfire Forge, a practitioner can bind a Spiritus Gem directly into a piece of equipment, granting it the ability to store Spiritus internally. Capacity matches the gem tier: Petty 64, Lesser 256, Common 1,024, Greater 4,096, Grand 16,384.

Place any wearable armor, tool, weapon, or shield alongside a Spiritus Gem in the Hellfire Forge to infuse it (the gem is consumed). Infused equipment stores Spiritus directly, drawn upon by Sentient Tools and other Spiritus-consuming effects just as it would be from a gem in your inventory. A colored bar at the top of the item icon indicates current charge level; bar color reflects the Aspect stored. Recharge via a Vas Maleficum or by absorbing monster souls.

## Blood Mending

Bind the restorative power of Essentia Vitae directly into equipment. An item imbued with **Blood Mending** slowly knits itself back together, drawing EV from the wielder's **[Anima](Blood-Orbs-and-Anima)** each second. The enchantment requires a bound Orb of Vitae in the practitioner's inventory and sufficient reserves to function; only worn armor and held items benefit.

Place the item to enchant in the Hellfire Forge alongside a Tabula Robur, Lapis Lazuli, and Nether Wart. The forge requires a minimum of **200 Spiritus** and consumes **400 Spiritus** in the process. The item retains all of its existing enchantments and properties.

See also: **[Hellfire Forge and Sentient Equipment](Hellfire-Forge-and-Sentient-Equipment)**, **[Rituals](Rituals)** (Crystallum Fractura, Lost Souls), **[Blood Orbs and Anima](Blood-Orbs-and-Anima)** (the parallel EV network).
