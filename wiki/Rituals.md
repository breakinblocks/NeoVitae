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

Breaking an inscribed stone returns a **blank** Ritual Stone rather than the inscribed one, so a dismantled circle goes straight back into building the next. The Diviner re-inscribes whatever it places, so blanks are all you ever need to carry. Use the Inscription Tools if you want a specific element kept for decoration.

The **Ritual Diviner** is the master architect's wand. Tap Use while aiming at a Master Ritual Stone and the Diviner constructs the selected ritual, consuming Ritual Stones from your inventory. It can clear soft obstructions like tall grass and snow, but not solid blocks. The base Diviner requires one of each Elemental Inscription Tool (1,000 EV each, Tier 2 altar). For the most advanced patterns, upgrade with **Tenebrae Inscription Tools** (2,000 EV each, Tier 3 altar). The Diviner and its Tenebrae variant are inexhaustible.

You can also change the facing of the completed ritual by pressing Use in the air; this only matters for asymmetrical rituals such as the Ritual of Speed.

## Ritual Configurator

The **Ritual Configurator** is essential for mastery over your circles. Cycle between three modes with Sneak + Use:

- **Information** - reveals the purpose of the selected ritual.
- **Set Spiritus Consumed** - attunes the ritual to consume specific aspects of **[Spiritus](Spiritus-Aspects-and-Crystals)** from the Aura. Carry the desired Spiritus Crystals in your hotbar, one per aspect.
- **Define Area** - specifies the zone in which the ritual operates and displays the current boundaries. Sneak + Use in the air to cycle through a ritual's zones, then click two opposite corners to reshape the selected one.

Rituals that draw from or deposit into a container expose their chest, tank, or input/output zone here as well, so you can relocate that container instead of being forced to place it directly above the Master Ritual Stone. It defaults to the block above the stone and can be moved a few blocks away.

Some rituals can be expanded far beyond their default range, but EV cost scales to match. Tread carefully with your reserves.

## Ritual Catalog

### Combat and Soul Harvest

| Ritual | Effect |
|--------|--------|
| Well of Suffering (*The Crimson Tithe*) | Harvests Essentia Vitae from the suffering of nearby creatures. |
| Ritual of the Willing Sacrifice (*Blood Freely Given*) | Converts the practitioner's own vitality into EV. |
| The Torment Nexus (*Why It Exists*) | Server-friendly endgame EV. Reads the configurations of nearby vanilla **and** Trial Spawners, simulates the kills they would produce, and feeds the resulting EV directly to your altar; no entities are spawned, no chunks load up with corpses. |
| The Ritual of Lost Souls | Watches a 21×21×21 area around the Master Ritual Stone for the moment of any non-player creature's death and drops a charged **Raw Spiritus** item at the death position. The richer the kill (Wither, Ender Dragon, Warden), the more Spiritus per drop. EV cost scales with the number of deaths processed each tick. |
| Ritual of Containment (*The Invisible Cage*) | Imprisons creatures within an invisible barrier. |
| Ritual of Expulsion (*The Warding Gale*) | Drives all creatures from your sanctum. |

### Automation and Resources

| Ritual | Effect |
|--------|--------|
| Ritual of Fallen Trees (*The Silent Axe*) | An unseen force fells every tree in its domain. |
| Ritual of Harvest (*The Reaper's Bounty*) | Reaps and replants mature crops. |
| Ritual of the Shepherd (*The Tending Circle*) | Hastens the growth of young animals and coaxes ready adults into breeding, spending a little EV to feed them (no food or chest required). Raw Spiritus quickens its pulse. |
| Ritual of Butchering (*The Culling*) | Reaps grown animals in range and gathers their drops into a chest above the stone, while sparing a breeding stock. Set how many of each species to keep with the Ritual Configurator. |
| Ritual of Overgrowth (*Verdant Awakening*) | Suffuses the earth with life, hastening all growth. |
| The Endless Quarry (*Deep Earth Communion*) | A patient, incremental ore drain. The ritual scans the column **beneath** the Master Ritual Stone (square footprint, **bedrock-deep**) and reaps each ore it finds. **Collection priority:** if a container (chest, barrel, ender chest, modded storage, etc.) sits **directly on top** of the master stone, the ore is inserted into it as an **item**; one ore block becomes one ore item. If there is no container, or the container is full, the ritual falls back to placing the ore **as a block** in the first empty slot of a **3×3×3 placement volume** directly above the master stone. **Cost:** 50 EV per ore moved. **Pacing:** up to **3 ores** and **100 block checks per refresh** (refresh every 40 ticks / 2 s) with a saved cursor so a full sweep resumes across many refreshes. **Scan radius** scales with the block placed directly beneath the master stone: default = 3 (7×7 footprint), iron block = 7 (15×15), gold block = 15 (31×31), diamond block = 31 (63×63), **netherite block = 63 (127×127)**. Unloaded chunks are pulled into memory as the scan reaches them, so a quarry tucked into a corner of base will still mine columns far from any player. Ores in claim-protected territory are left alone. |
| Hymn of Siphoning (*The Thirsting Stone*) | Draws surrounding fluids into a waiting vessel. |
| Ritual of the Mason (*The Mason's Spiritus*) | Places blocks drawn from an adjacent container into the empty spaces of its area. **Shape** is chosen in the Ritual Configurator: Solid, Shell (outer skin only), Floor, Walls, Roof, or Frame (the twelve edges). An area one block thick has no inside, so Shell fills it solid and Frame traces its outline. **Raw Spiritus** in the chunk sets both the pace and how large an area you may configure: none = 1 block per pulse and 5,000 blocks of area, 20 raw = 4 per pulse and 20,000 (reach 24), 50 raw = 8 per pulse and 80,000 (reach 40). The aura is spent as it works (0.01 raw per block), so the ritual settles at whatever pace your supply sustains; it never stalls, only slows. If the aura lapses you keep the area you configured but cannot enlarge it until the Spiritus returns. Costs 10 EV per block placed. Blocks that cannot survive where they would land are skipped rather than destroyed. |
| Rhythm of the Beating Anvil (*The Tireless Smith*) | Automates crafting through vitaemantic labor. |
| Ritual of the Full Spring (*The Spring Eternal*) | Places water source blocks in the world; while any **Raw Spiritus** aura is present in the chunk, also fills any fluid tank directly above the Master Ritual Stone with water (1,000 mB per refresh). |
| Serenade of the Nether (*Infernal Invocation*) | Tears open a seam to the molten depths. |
| Ritual of the Satiated Stomach (*The Inexhaustible Feast*) | Feeds all practitioners from a nearby larder. |
| Crystallum Fractura | Harvests mature crystal clusters and weaves a 2x growth aura + 1.25x spiritus injection across the chunks in range. See **[Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals)** for aspect bias details. |
| Ritual of Enchanted Vitae | Throw one item and any number of enchanted books onto the 5x2x5 area above the Master Ritual Stone. The rite reads the books and binds every enchantment the item can carry, then returns the books unspent. There is no level ceiling: a Sharpness 99 book binds Sharpness 99. Where two books offer the same enchantment the higher level wins, and an offer no better than what the item already holds is passed over. **Conflicts halt the rite.** If two offered enchantments cannot share an item, or an offer quarrels with something already on the item, nothing is bound: the owner is told which pair is at fault and the books carrying them glow for 10 seconds. A halted rite does **not** restart when you remove a book, so sweeping up a good book alongside the bad one cannot commit a partial set. Lift the item off the stone and offer it again to resume; the rite then names exactly what it is about to bind before the five-second charge. Cost is 2,000 EV per bound level, multiplied by how rare the enchantment is (up to 5x for the rarest), taken in one draw when the rite completes. If your network cannot cover the cost, nothing is bound. |

### World-Shaping and Discovery

| Ritual | Effect |
|--------|--------|
| Dawn of the New Moon (*The Lifted Earth*) | Scoops a solid ellipsoidal volume of terrain from **below** the Master Ritual Stone and teleports it **upward** to float above the ritual, leaving a matching ellipsoidal void beneath. Each lifted block drains **10 EV**; up to 100 blocks checked per refresh, with the cursor saved between refreshes so a full lift completes incrementally. The **size of the lifted moon is set by the block directly beneath the master stone**: default = 33 across, iron block = 41, gold block = 49, diamond block = 57, **netherite block = 65**. The source volume sits two blocks below the master and extends straight down for the full diameter; the destination floats two blocks above and rises the same height. Claim-protected source blocks are skipped, and the destination is only populated where claim rules permit placement. The ritual auto-stops once the full volume has been swept. |
| Ritual of Meteo (*Heaven's Wrath*) | Watches for a specific catalyst item dropped within the ritual area, consumes it, and crashes a corresponding meteor from above. The meteor's sphere comes to rest with its **lowest block one block above the Master Ritual Stone**, so the ritual circle is preserved. The exact landing altitude is calculated per-catalyst from the recipe's outermost sphere radius. Catalysts are defined by **meteor recipes** (consult JEI); each names the meteor that answers. |
| All Consuming Void (*The Devouring Maw*) | Erases blocks one at a time from a small 3×3×3 box directly beneath the Master Ritual Stone. **No drops, no items left behind, no echo of what was there.** Volume can be expanded with the Ritual Configurator to a reach of 64 blocks down and 32 wide. 10 EV per block consumed; default refresh is 10 ticks, accelerated by **Raw Spiritus** in the chunk (down to one tick at high saturation). **Spiritus modes:** **Spiritus Invictus** moves the block to a 3×3×3 placement volume above the master stone instead of consuming it. **Spiritus Ruina** reads items in a chest above the master stone as a whitelist, consuming only matching blocks. Claim-protected blocks are skipped. |
| Breaching the Edge of Demon Realm (*A Crack in the Veil*) | One-shot rite that assembles a **Starter tier dungeon** structure at the Master Ritual Stone, the entry tier of the Demon Realm experience. Consumes a large EV pool. |
| Highway to Hell (*Beyond the Threshold*) | Opens a permanent gateway to the **Endless tier dungeon**: a vast procedural dungeon (Mines, Foreman fight, aspected loot) that goes on forever and can be returned to as often as you like. Consumes a very large EV pool to forge the gateway. |

### Buffs and Practitioner Effects

| Ritual | Effect |
|--------|--------|
| Soaring Skies (*Wings of the Condor*) | Bestows true flight upon all within the circle. |
| Ritual of Speed (*Quickened Blood*) | Hurls every non-sneaking entity in the area in the master stone's facing direction. **Sneak** within the area instead and the ritual applies Speed II for 30 minutes; useful for transit or as a launcher cannon, depending on stance. Spectators are ignored. **Tip:** mount on an **Inverted Master Ritual Stone** and trigger with a pressure plate or button so the launcher only fires while the signal is active. |
| Ritual of Regeneration (*The Mending Circle*) | Mends the wounds of all within its reach. |
| Ritual of the Phantom Bridge (*Spectral Pathways*) | Weaves spectral platforms beneath your feet. |
| The Gathering (*The Hoarder's Breeze*) | A persistent wind that gathers all loose items. |

### Suppression and Denial

| Ritual | Effect |
|--------|--------|
| Dome of Suppression (*The Parted Tide*) | Holds all fluids at bay within a warded dome. |
| The Sinner's Burden (*The Weight of Guilt*) | Denies the sky to all within its domain. |

### Sentient Armor Workings

| Ritual | Effect |
|--------|--------|
| Ritual of Sentient Evolution (*The Sound of Becoming*) | Expands a worn Sentient Armor's upgrade capacity: each activation adds 100 Upgrade Points, from the 100-point baseline up to a maximum of 500. The ritual deactivates after each evolution and refuses armor already at the cap. See **[Sentient Armor](Sentient-Armor)**. |
| Ritual of Sentient Penance (*The Willing Burden*) | Stand on the Master Ritual Stone wearing your Sentient chestplate and throw a **downgrade catalyst** onto the small 5×2×5 area above it. Each pulse consumes one catalyst and inscribes one level of the matching **Downgrade**, freeing Upgrade Points to spend elsewhere. See **[Sentient Armor](Sentient-Armor)** for the catalyst list. |
| Sentient Extraction (*The Price of Power*) | Throw a piece of Sentient Armor onto the small 5×2×5 area above the Master Ritual Stone. The ritual extracts every upgrade and downgrade currently inscribed on it as separate **Upgrade Tomes** (one per upgrade, preserving accumulated experience), then strips the armor clean. The tomes can later be inscribed back onto a fresh chestplate. |
| Tabula Rasa (*Purification of Form*) | Stand on the Master Ritual Stone wearing Sentient Armor. The ritual wipes **every upgrade** from every Sentient piece you have equipped and resets used points to zero, giving you back a clean slate at the cost of all accumulated training. **No tomes are produced**; if you want to preserve the upgrades for later, use **Sentient Extraction** instead. |

## Animal Husbandry: A Self-Sustaining Farm

Two rituals work hand in hand to run an animal farm that needs no tending of your own: the **Ritual of the Shepherd** and the **Ritual of Butchering**. Both default to the same 11x3x11 footprint, so building them over the same pen lets them act on the one flock.

**The Shepherd** keeps the herd growing. Each pulse it advances any young animals toward adulthood and sets ready adults to breeding, paying a small amount of EV per animal in place of feed, so it needs no food and no chest. A chunk rich in Raw Spiritus quickens its pulse.

**The Butchering** keeps the herd in check. Each pulse it slays grown animals and gathers their drops into a chest set above its Master Ritual Stone. It counts the pen by species and only culls a kind while more than your chosen number of that kind remain, so a breeding stock always survives. Set that number with the **Ritual Configurator**: right-click the stone and adjust the **Keep per species** dial.

Run together over one pen, the Shepherd replaces what the Butchering takes. The young grow, the adults breed, the surplus is reaped into your chest, and the breeding stock you set is never touched. Walk away and return to a steady supply of meat, leather, wool, and whatever else your animals drop, with the flock none the smaller for it.

Practical notes:

- Both rituals build from standard runes and activate with a standard Activation Crystal, so neither needs an advanced diviner.
- The Butchering wants a chest directly above its stone to collect drops; without one, the drops simply fall in the pen.
- Fence the pen so bred animals stay within range of both circles.

See also: **[Spiritus, Aspects and Crystals](Spiritus-Aspects-and-Crystals)** (for the aura many rituals draw on), **[Sentient Armor](Sentient-Armor)** (armor-evolution rituals), **[Blood Orbs and Anima](Blood-Orbs-and-Anima)** (the EV source), **[Ara Vitae and Runes](Ara-Vitae-and-Runes)** (comparator + redstone shutdowns).
