# Roadmap

A working list of planned reworks, new systems, and ideas. Order is indicative, not promised; everything below is subject to change.

## Tier 1: Core System Reworks

### Living Spiritus Fluid Creation

A new crafting mechanic for **Living Spiritus**: liquefied will produced and consumed by a new process, layered alongside the existing systems rather than replacing anything. Open design questions: how is it produced (Vas Maleficum variant? new dedicated multiblock?), what consumes it, and whether aspect tinting carries through the fluid form.

### Sentient Armor -> Manifestation Baubles

Decouple the training system from the armor slot so players can wear other mods' chestplates alongside it. The trained "manifestation" becomes a Curios item carrying the existing `UpgradeTome` progress and applying its effects through attribute modifiers and event hooks instead of armor slot bindings. Preserves the unique training arc while killing the armor-slot conflict that currently locks players out of cross-mod gear. Bonus: opens the door for *multiple* equipped manifestations later (perhaps slot-typed: combat, mining, survival).

### Sanguine Sorcery (Spell System)

A new spell framework where players assemble effects from components and bind them to a casting sigil. Different casting sigils of various tiers can hold more spells than the previous tiers and may be imbued with different types of spiritus to alter their effects, fueled by EV + aspect spiritus. Replaces the consumable-effect items (anointments, charges) with a player-owned, configurable system that scales with progression rather than throwaway charges. Possible architecture: spell-component datamap + per-effect cost/cooldown, with a casting GUI driven by held focus and modifier keys. Will need custom animations and some artwork/particles/sound fx to really enforce and enhance the magical aspect of it.

### Anointments -> Sanguine Sorcery Migration

Anointment effects (silk touch, fortune, smelting, etc.) become spell components instead of one-shot item modifiers. Delete the anointment items after Migration. Implementation path would be something like existing anointed items convert to components used to unlock spells.

### Ritual Diviner GUI Selector

Replace the current "cycle through rituals with sneak-right-click" UX with a proper screen: searchable list, filter by tier/category/aspect, preview of multiblock layout and runtime cost. Hooks the existing `RitualLayouts` datamap so packs adding rituals automatically appear without further work.

## Tier 2: Player Feedback & UX

### EV Gain Flytext

Floating text above the player when EV enters their network from a **direct** action (sacrifice, dagger kill, orb-offhand kill). Suppress automated sources (Well of Suffering, sacrifice rituals running on timer) so the screen doesn't become spam. Hook `BloodSiphonHandler.onEntityKilled` and the dagger's `LivingDeathEvent` path. Configurable on/off per-player.

### EV Network Inspector Device

A "scrying scope" or magic display block that visualizes the owner's EV economy: live net gain/loss per minute, breakdown of sources and consumers, distance/dimension of each consumer, and warning glyphs for runaway drains. Backed by per-player tick samples already implicit in `Anima.syphon` / `Anima.add`: formalize a ring buffer of recent transactions tagged by source.

### Blood Orb Scrying

Right-click a blood orb on a remote pos (sigil-bound or marker-bound) and view chunk/altar/ritual state through it without traveling. Limited radius per orb tier. Pairs naturally with the EV inspector (WIP name): share the GUI shell.

### Remote Interactions

Once scrying exists, the next step is acting at range: configure an altar's runes, toggle a ritual, open a routing node's GUI, all via a scrying focus. Costs EV per action.

## Tier 3: Routing & Automation

### Redstone Channels for Routing Nodes

Frequency-based wireless redstone between routing nodes. Each node can be tuned to a channel + role (emitter / listener) and packs can config channel counts. Solves a major automation pain (running long redstone lines through built bases).

### Nested Filter Support for Routing Nodes

Filters that contain other filters, with boolean composition (AND/OR/NOT). Lets you express "iron tools but not damaged" or "anything tagged food except mob drops". Backwards-compatible with existing flat filters (treat them as a degenerate single-leaf nesting).

### Dynamic Mod-Ore Support in Meteor Recipes

Extend `MaterialRegistry`'s ore discovery to feed meteor recipes too: any ore added to `c:ores/<material>` becomes a valid Meteor of Spiritus target without a pack dev hand-writing JSON. Use the same color extraction logic for visuals.

## Tier 4: Combat & Content

### Sigil of Transfiguration

Datamap-driven mob -> mob conversion (e.g. pig -> zombified piglin, cow -> mooshroom, custom: zombie -> daemonium variant). Datamap entry: input entity, output entity, cost, optional reagent items. Lore hook: forced manifestation of aspect resonance.

### Greater Sigil of Holding

Bigger than current Sigil of Holding: more sigil slots, optionally with sigil-stacking (multiple of the same active at once with diminishing returns), or "loadouts" the player can hot-swap.

### Mortifex Endgame Boss

Stronger than the Foreman, occupies the existing prebuilt boss room. Suggested kit: phase-shifting based on chunk spiritus aspect, summons aspect-pure daemoniums, environmental hazards keyed to the alchemical devices below. Drops the gating item for new endgame tier content (Heretical Altar, capstone manifestation, etc.).

### Dungeon-Only Alchemical Devices

Functional blocks that only operate inside the dungeon dimension: e.g. **Stillpool** (refunds 50% spiritus cost while standing in claim), **Hourstone** (slows ritual cooldowns), **Vorhand** (shares EV with party members in radius). Encourages bringing fights into the dungeon rather than camping outside. Tie cleanly to the existing `DungeonSavedData`.

### Ritual Spiritus Tuning UX

Better in-world feedback for what spiritus a ritual needs and what aspect bias is currently applied. Hover-on-Master-Ritual-Stone tooltip showing the local chunk aspect breakdown vs. the ritual's preferred ratio, color-coded; chat message when activation fails due to insufficient aspect rather than the current generic "no spiritus".

## Tier 5: Documentation

- Continued in-game guide (Modonomicon) updates for new systems.
- Wiki page per major new system as it ships.
- Migration notes per release for pack devs (especially the Sanguine Sorcery cutover).

## Additional Ideas

### 1. Ritual Reagents

Single-use items dropped into a Master Ritual Stone to modify the **next** tick: radius +N, free cost, force-aspect, instant-refresh. Datamap-driven so packs can author new reagents. Great pressure-release valve for late-game players: cheap reagents for common rituals, expensive ones for the powerful effects.

### 2. Anima Vessels (Captureable Companions)

Bind a defeated daemonium's residue into a vessel item; the vessel can be summoned as a temporary follower (combat, scouting, item retrieval). Lifetime measured in EV drained per tick. Datamap-driven entity -> vessel mapping so packs extend.

### 3. Spiritus Conduits

Placeable blocks that pipe spiritus between non-adjacent chunks. Loss factor per block and per cross-dimension hop, set by aspect (Ruina cheap, Invictus expensive). Solves the "Vas Maleficum has to be on top of the consumer" placement constraint.

### 4. Personal Sanctum Sigil

Sigil that binds a personal pocket sanctuary (small generated dimension cell or claimed chunk). Acts as the player's safe staging ground for ritual prep.

### 5. Aspect Ascension Perks

Per-aspect player progression tracked via the Anima: harvest N Ruina crystals, complete N rituals with Invictus bias, etc. Each milestone unlocks a passive perk (faster crystal growth in your loaded chunks, cheaper aspect costs, etc.). Visible in a new tab in the EV inspector.

### 6. Aspect Resonator Multiblock

A new mid-game multiblock that *transmutes* one aspect of spiritus into another at a configurable loss ratio. Currently the only way to get all five aspects is to source-collect each; this provides a single-aspect-farm path with an efficiency tradeoff. Output ratio set by altar tier / configured rune assist.

### 7. Leyline Networks

Decorative + functional placeable "leyline" blocks that visibly chain rituals into a network. Pass spiritus, EV, or ritual triggers between Master Ritual Stones with line-of-sight requirements. Heavy build-aesthetic appeal plus genuine logistical use. Pairs well with Ritual Linking under the same UI.

### 8. Schematic Wand (Magical Multiblock Builder)

Capture a multiblock you've built into a schematic crystal; later, consume the crystal + the listed materials + EV to place the multiblock elsewhere. EV cost scales with block count. Especially useful for the more elaborate ritual circles and the new altar tiers, and for moving builds between Sanctums.

### 9. Daemonium Reliquaries

Boss-room loot from Mortifex and Foreman: an item that summons a tamed (player-bound) version of that boss into the world for a limited engagement, costing significant EV. Effectively a "boss replay" mechanic for players who farm the dungeon but want to invite friends to fight bosses they've already cleared.

### 10. Cross-Mod Compat

Targeted hooks for major modpack staples:

- **Curios**: surface Sentient Manifestation slot if Curios is present.
- **Mekanism / Create**: tag-compat for dust/ingot interchange so Athanor materials feed into existing processing.
- **JADE**: per-block tooltip showing ritual state, altar EV, etc. tied in with divination sigil

## Tentative Priority

1. **Routing QoL** (redstone channels, nested filters): quick wins, no system churn.
2. **EV flytext + Inspector device**: player visibility upgrade, prerequisites for scrying.
3. **Dynamic meteors**: pack-dev quality of life.
4. **Ritual Diviner GUI**: single screen, isolated rewrite.
5. **Sanguine Sorcery** + anointment/charge migration: the biggest cohesive rework; do as one major version.
6. **Sentient Manifestation**: depends on whether you want it before or after Sanguine Sorcery; can be parallel.
7. **Living Spiritus Fluid**: natural follow-up once Sanguine Sorcery exposes spiritus consumers.
8. **Dungeon expansion**: Mortifex, alchemical devices, vessels.
9. **Long-tail content & cross-mod compat**: sigils, reagents, conduits, perks.
