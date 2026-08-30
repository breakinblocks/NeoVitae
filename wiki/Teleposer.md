# Teleposer

> *Bend space with blood, and the world rearranges itself around the wound.*

The **Teleposer** is NeoVitae's spatial-exchange block. It does not teleport an entity through space the way a sigil might; instead, it **swaps everything above two linked Teleposers in a single instant**, blocks, items, entities, and you. Used well, it is the backbone of vertical elevators, hidden transit networks, and surgical structure relocation. Used poorly, it is an expensive way to drop yourself into lava.

For mass-flow logistics of *items, fluids, and energy*, see the [Routing Network](Routing-Network) instead.

## The Basic Loop

The Teleposer is one of the simpler magic-tech blocks in the mod, but it does have a few moving parts. The basic activation loop:

1. **Place two Teleposers** one at the source and one at the locations you wish to link to.
2. **Craft a Teleposition Focus** sized appropriately to what you wish to swap (see below).
3. **Bind the Focus** to the target Teleposer by right-clicking the target block with the Focus in hand.
4. **Place the bound Focus** inside the *source* Teleposer.
5. **Apply a redstone signal** to the source Teleposer. The exchange happens instantly.

The volume above each Teleposer is swapped. Blocks above source A appear above source B, and vice versa. Entities standing in the volume go with the blocks they are standing on.

## Cost

Nothing in Vitaemancy is free. The Teleposer pulls from your bound [Anima](Blood-Orbs-and-Anima):

| Cost type | Value |
| --- | --- |
| Per block or entity | **1 EV per 2 blocks of distance** |
| Per object cap | 1,000 EV |
| Per activation cap | 10,000 EV |

A short hop in your tower will run a handful of EV. A cross-base relocation of a populated structure will eat most of a Tier IV orb. Plan accordingly.

## Teleposition Focus Tiers

The Focus determines the **volume** that gets swapped. All three Focus tiers are crafted on the [Ara Vitae](Ara-Vitae-and-Runes); the bigger volumes require correspondingly higher altar tiers.

| Focus | Volume | Typical use |
| --- | --- | --- |
| **Teleposition Focus** | **1×1×1** | A single block, a single entity, precision drops |
| **Enhanced Teleposition Focus** | **3×3×3** | Small structures: a furnace stack, a spawner room |
| **Reinforced Teleposition Focus** | **5×5×5** | Sizeable buildings: an entire workshop wing |

A Focus, once bound to a target Teleposer, retains that binding until you re-bind it. You can keep a handful of pre-bound Foci on your hotbar, each pointing at a different destination, and swap them in and out of a source Teleposer as needed.

## Redstone Activation

Any redstone signal activates the Teleposer: redstone dust running into the side, a repeater or comparator pointed at the block, a lever or button placed directly on it, or an adjacent redstone block.

The Teleposer fires on the rising edge of the signal, so a sustained signal produces exactly one exchange. The signal must drop back to zero before the next activation.

If you build a Teleposer-driven elevator, the cleanest trigger is **a button on the side of the Teleposer itself**: it self-clears, ready for the next press.

## Topologies of Transport

Teleposers may be linked in many configurations:

- **One-way**: only the source has a Focus. The destination Teleposer is inert; you arrive but cannot return through the same pair.
- **Two-way**: a Focus in each Teleposer pointing at the other. Activate either end, the swap happens, and the Foci come along (since they are blocks inside the Teleposer's volume… or rather, they remain in their respective Teleposers, which are *not* in the swap volume). Either side can initiate.
- **Chained**: A points to B, B points to C, C points to A. Useful for round-trip patrol routes or multi-floor elevators.
- **Vertical elevators**: stack pairs along your tower's central axis. A single column can move you, your inventory, and a Storage block of choice from the basement to the rooftop in one redstone tick.

Be creative. The block does not care what shape your network takes; only that the cost is paid and the redstone fires.

## Protected Blocks: the Blacklist Tag

Not everything is willing to be transposed. Bedrock, end portals, command blocks, and similar critical-infrastructure blocks are **rejected** by the exchange. The full list is governed by the **`#neovitae:telepose_blacklist`** block tag, which pack authors can extend or shorten to taste.

If a Teleposition swap would move a blacklisted block, the entire activation is aborted; the EV is *not* spent. This is intentional; the failure is loud, not silent.

## Relocating Spawners

**Mob spawners and trial spawners count as ordinary blocks** to the Teleposer, and their full settings ride along, including entity NBT, spawn counts and cooldowns, current cooldown state, and any chained trial-spawner behavior. Build your grinder chamber elsewhere, then transpose the spawner *into* it.

Trial spawners from the [Endless Dungeon](The-Endless-Dungeon) can be relocated this way, but the mob list is locked at world-load; the spawner behaves in the new location exactly as it did in the dungeon. Some packs extend `#neovitae:telepose_blacklist` to lock spawners in place; check your pack's tag overrides if a swap unexpectedly fails.

## Practical Patterns

### The Two-Block Elevator

Place a Teleposer in your floor and another in your ceiling, directly above. Bind a 1×1×1 Focus to each pointing at the other, and load each with its respective Focus. Add a button on the side of each. Press the ground button to swap to the upper position; press the upper button to come back. Cost: 1 EV per direction.

### The Workshop Mover

Pair a 5×5×5 Reinforced Focus with two Teleposers, one centered in your existing workshop, one in a prepared site. A single button press relocates the workshop wholesale, machines, chests, redstone, and all.

### The Hidden Transit Hub

A central Teleposer chamber under your base, with one Teleposer per remote site, each with a pre-bound Focus. Step in at any site to arrive at the hub; swap in a different Focus and step in again to reach any other site. A personal subway gated only by EV.

## Practical Tips

- **Test every link with a non-living block first** (a single torch in the swap volume) before you trust a new Foci to your own body.
- **Mark your destinations.** The Teleposer at the far end of a long link is easy to lose track of. A colored wool block, a sign, or a unique frame helps.
- **Carry a spare Focus** if you depend on Teleposer transit. Foci are crafted, not infinite, and a lost Focus can strand you.
- **Mind the cost cap.** A single activation cannot spend more than 10,000 EV; if a 5×5×5 swap of dense blocks would cost more, the activation aborts. Use a higher orb tier or shorten the distance.
- For *items in bulk*, the [Routing Network](Routing-Network) is cheaper and more flexible. The Teleposer's niche is **moving the world itself**, not its contents.
