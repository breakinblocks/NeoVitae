# Rituals

When your **[Ara Vitae](Ara-Vitae-and-Runes)** reaches Tier 2, the boundaries of Vitaemancy expand dramatically. You gain access to **Rituals**, vast sigil-circles that channel your Anima into sustained, powerful effects upon the world itself.

## The Art of Ritual

To perform a ritual you require:

- An **Activation Crystal**. At Tier 2, only the Weak Crystal is available.
- A **Master Ritual Stone** (exactly one, at the heart of every circle).
- Sufficient **Ritual Stones** to form the pattern.
- A **Ritual Diviner** (strongly recommended). Hold Sneak and press Use or Attack while looking at empty air to cycle through available rituals. Check rune requirements by hovering over the Diviner in your inventory while sneaking.

Place the Master Ritual Stone, then hold Use with the Diviner aimed at it until every stone has been placed and inscribed with the correct element. Finally, press Use on the Master Ritual Stone with your Activation Crystal in hand. If the circle is complete, you will feel a rush of energy and the ritual awakens.

If you instead feel a push but are too weak, your Anima lacks the EV needed for activation. Fill your reserves and try again. If the runes feel misconfigured, something obstructs or misaligns the pattern; clear the area and reassemble with care. Remember that some rituals extend several blocks above and below the Master Ritual Stone.

If nothing happens at all, ensure the crystal is bound to an Anima; press Use while holding it to bind it to yours. A crystal need not be bound to your own Anima; if you acquire another Vitaemancer's crystal, you can activate rituals using their EV. Guard yours well.

All rituals respond to a redstone signal. A lever on the Master Ritual Stone provides a simple means of silencing it; combine this with redstone automation (see **[Ara Vitae and Runes](Ara-Vitae-and-Runes)**'s comparator section) to ensure rituals shut down before your Anima runs dry.

## Activation Crystals

A properly inscribed circle is inert without the spark of will to awaken it. The Activation Crystal serves as that spark, opening a conduit between your Anima and the waiting runes.

- **Weak Activation Crystal** - forged in the Ara Vitae from a Lava Crystal. Activates basic rituals.
- **Awakened Activation Crystal** - resonates with deeper currents; required for advanced rituals. Forged in the Ara Vitae from a Weak Crystal.

## Ritual Stones and the Diviner

**Ritual Stones** are the foundation upon which every circle is inscribed. Each serves as a vessel for elemental resonance. The **Elemental Inscription Tools** can paint stones by hand and never break, so feel free to use them for decoration as well as function.

The **Ritual Diviner** is the master architect's wand. Tap Use while aiming at a Master Ritual Stone and the Diviner constructs the selected ritual, consuming Ritual Stones from your inventory. It can clear soft obstructions like tall grass and snow, but not solid blocks. The base Diviner requires one of each Elemental Inscription Tool (1,000 EV each, Tier 2 altar). For the most advanced patterns, upgrade with **Dusk Inscription Tools** (2,000 EV each, Tier 3 altar). The Diviner and its Dusk variant are inexhaustible.

You can also change the facing of the completed ritual by pressing Use in the air; this only matters for asymmetrical rituals such as the Ritual of Speed.

## Ritual Tinkerer

The **Ritual Tinkerer** (also known as the Ritual Reader) is essential for mastery over your circles. Cycle between three modes with Sneak + Use:

- **Information** - reveals the purpose of the selected ritual.
- **Set Spiritus Consumed** - attunes the ritual to consume specific aspects of **[Spiritus](Spiritus-Aspects-and-Crystals)** from the Aura. Carry the desired Spiritus Crystals in your hotbar, one per aspect.
- **Define Area** - specifies the zone in which the ritual operates and displays the current boundaries. If multiple zones exist, Sneak + Use on the Master Ritual Stone cycles between them.

Some rituals can be expanded far beyond their default range, but EV cost scales to match. Tread carefully with your reserves.

## Ritual Catalogue

### Combat and Soul Harvest

| Ritual | Effect |
|--------|--------|
| Well of Suffering (*The Crimson Tithe*) | Harvests Essentia Vitae from the suffering of nearby creatures. |
| Ritual of the Feathered Knife (*The Willing Sacrifice*) | Converts the practitioner's own vitality into EV. |
| The Torment Nexus (*Why It Exists*) | Server-friendly endgame EV. Reads the configurations of nearby vanilla **and** Trial Spawners, simulates the kills they would produce, and feeds the resulting EV directly to your altar; no entities are spawned, no chunks load up with corpses. |
| Gathering of the Forsaken Souls | Watches a 21×21×21 area around the Master Ritual Stone for the moment of any non-player creature's death and drops a charged **Raw Spiritus** item at the death position. The richer the kill (Wither, Ender Dragon, Warden), the more Spiritus per drop. EV cost scales with the number of deaths processed each tick. |
| Ritual of Binding (*The Invisible Cage*) | Imprisons creatures within an invisible barrier. |
| Aura of Expulsion (*The Warding Gale*) | Drives all creatures from your sanctum. |

### Automation and Resources

| Ritual | Effect |
|--------|--------|
| Crash of the Timberman (*The Silent Axe*) | An unseen force fells every tree in its domain. |
| Reap of the Harvest Moon (*The Reaper's Bounty*) | Reaps and replants mature crops. |
| Ritual of the Shepherd (*Nurturing Pulse*) | Hastens the maturation of young creatures. |
| Ritual of the Green Grove (*Verdant Awakening*) | Suffuses the earth with life, hastening all growth. |
| Ritual of Magnetism (*Deep Earth Communion*) | Persistent pulling field. Every loose item entity within a 21×7×21 box around the Master Ritual Stone is dragged toward the stone, letting you funnel mob drops, ore-processing outputs, or any item rain into a single collection point. |
| Hymn of Siphoning (*The Thirsting Stone*) | Draws surrounding fluids into a waiting vessel. |
| Domain of the Filler (*The Mason's Spiritus*) | Fills the void with blocks drawn from a chest. |
| Ritual of the Crusher (*The Grinding Pressure*) | Grinds blocks to dust and collects the spoils. |
| Rhythm of the Beating Anvil (*The Tireless Smith*) | Automates crafting through vitaemantic labor. |
| Ritual of the Full Spring (*The Spring Eternal*) | Places water source blocks in the world; with **Raw Spiritus** available in the chunk, also fills any fluid tank directly above the Master Ritual Stone (1 Raw Spiritus per 1,000 mB of water). |
| Serenade of the Nether (*Infernal Invocation*) | Tears open a seam to the molten depths. |
| Ritual of the Satiated Stomach (*The Inexhaustible Feast*) | Feeds all practitioners from a nearby larder. |
| Crystallum Fractura | Harvests mature crystal clusters and weaves a 2x growth aura + 1.25x spiritus injection across the chunks in range. See **[Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals)** for aspect bias details. |

### World-Shaping and Discovery

| Ritual | Effect |
|--------|--------|
| Focus of the Ellipsoid (*The Architect's Eye*) | Constructs perfect geometric forms from raw materials. |
| Dawn of the New Moon (*The Phantom Moon*) | Conjures a hollow sphere of spectral matter. |
| Mark of the Falling Tower (*Heaven's Wrath*) | Watches for a specific catalyst item dropped within the ritual area, consumes it, and crashes a corresponding meteor from above. Catalysts are defined by **meteor recipes** (consult JEI); each names the meteor that answers. |
| Yawning of the Void (*The Devouring Maw*) | Devours the earth, layer by insatiable layer. |
| Edge of the Hidden Realm (*A Crack in the Veil*) | Tier 0 dungeon generation rite. One-shot. Consumes a large EV pool and assembles a complete **Simple Dungeon** structure at the Master Ritual Stone, the entry tier of the Endless Dungeon experience. |
| Pathway to the Endless Realm (*Beyond the Threshold*) | Tier 0 dungeon generation rite. One-shot. Consumes a much larger EV pool than its lesser cousin and assembles a full **Standard Dungeon** structure, the main procedural dungeon with Mines, Foreman fight, and aspected loot. |

### Buffs and Practitioner Effects

| Ritual | Effect |
|--------|--------|
| Reverence of the Condor (*Wings of the Condor*) | Bestows true flight upon all within the circle. |
| Ritual of the High Jump (*Ascendant Leap*) | Grants practitioners the power to leap skyward. |
| Ritual of Speed (*Quickened Blood*) | Hurls every non-sneaking entity in the area in the master stone's facing direction. **Sneak** within the area instead and the ritual applies Speed II for 30 minutes; useful for transit or as a launcher cannon, depending on stance. Spectators are ignored. **Tip:** mount on an **Inverted Master Ritual Stone** and trigger with a pressure plate or button so the launcher only fires while the signal is active. |
| Ritual of Regeneration (*The Mending Circle*) | Mends the wounds of all within its reach. |
| Ritual of the Phantom Bridge (*Spectral Pathways*) | Weaves spectral platforms beneath your feet. |
| Call of the Zephyr (*The Gathering Wind*) | A persistent wind that gathers all loose items. |

### Suppression and Denial

| Ritual | Effect |
|--------|--------|
| Dome of Suppression (*The Parted Tide*) | Holds all fluids at bay within a warded dome. |
| The Sinner's Burden (*The Weight of Guilt*) | Denies the sky to all within its domain. |

### Sentient Armor Workings

| Ritual | Effect |
|--------|--------|
| Ritual of Sentient Evolution (*The Sound of Becoming*) | Expands a worn Sentient Armor's upgrade capacity beyond its former limits. See **[Sentient Armor](Sentient-Armor)**. |
| Penance of the Leaden Soul (*The Price of Power*) | Throw a piece of Sentient Armor onto the small 5×2×5 area above the Master Ritual Stone. The ritual extracts every upgrade currently inscribed on it as separate **Upgrade Tomes** (one per upgrade, preserving accumulated experience), then strips the armor clean. The tomes can later be inscribed back onto a fresh chestplate. |
| Sound of the Cleansing Soul (*Purification of Form*) | Stand on the Master Ritual Stone wearing Sentient Armor. The ritual wipes **every upgrade** from every Sentient piece you have equipped and resets used points to zero, giving you back a clean slate at the cost of all accumulated training. **No tomes are produced**; if you want to preserve the upgrades for later, use **Penance of the Leaden Soul** instead. |

See also: **[Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals)** (for the aura many rituals draw on), **[Sentient Armor](Sentient-Armor)** (armour-evolution rituals), **[Blood Orbs and Anima](Blood-Orbs-and-Anima)** (the EV source), **[Ara Vitae and Runes](Ara-Vitae-and-Runes)** (comparator + redstone shutdowns).
