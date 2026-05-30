# Pack Makers: Materials and Dungeons

This page covers the parts of NeoVitae customization that live outside the DataMaps and recipes, namely the data-driven material system, the KubeJS event hooks, custom dungeon room authoring, and the telepose blacklist tags. See [Pack Makers Overview](Pack-Makers-Overview) for the index and [Pack Makers DataMaps and Recipes](Pack-Makers-DataMaps-and-Recipes) for tag/recipe details.

## Data-Driven Material System

NeoVitae's ore processing system is fully data-driven. Materials (dusts, gravels, fragments) are defined in a single JSON config and items are generated **at startup** via an in-memory resource pack. See [Athanor and Materials](Athanor-and-Materials) for the player-facing system.

### Config File

**Location:** `config/neovitae/materials.json`

Each entry defines a processable material:

```json
{
  "name": "tin",
  "color": "#C8C8D0",
  "stages": ["fragment", "gravel", "dust"],
  "smelt_to": "create:tin_ingot",
  "smelt_xp": 0.7,
  "ore_tag": "c:ores/tin",
  "raw_tag": "c:raw_materials/tin",
  "ingot_tag": "c:ingots/tin",
  "display_name": "Tin",
  "generative": true,
  "gen_ore": "",
  "gen_raw": ""
}
```

| Field | Required | Description |
|-------|----------|-------------|
| `name` | Yes | Unique material name. Used in item IDs (`neovitae:tin_dust`). |
| `color` | Yes | Hex color for item tinting (e.g. `#C8C8D0`). |
| `stages` | Yes | Processing stages to create: `fragment`, `gravel`, `dust`. |
| `smelt_to` | No | Item ID for smelting output. Omit to skip smelting recipes. |
| `smelt_xp` | No | XP from smelting (default: 0). |
| `ore_tag` | No | Tag for ore blocks (e.g. `c:ores/tin`). Enables ore processing recipes. |
| `raw_tag` | No | Tag for raw materials (e.g. `c:raw_materials/tin`). |
| `ingot_tag` | No | Tag for ingots/gems (e.g. `c:ingots/tin`). Enables ingot-to-dust recipe. |
| `display_name` | No | Override display name. Defaults to capitalized material name. |
| `id_overrides` | No | Map to override generated item IDs per stage. |
| `generative` | No | Whether this ore feeds NeoVitae's generation systems (Prismatic Demonite drops, dungeon iron-ore deposits, meteors). Defaults to `true`. Set `false` to exclude the ore. |
| `gen_ore` | No | Preferred ore block id for generation (e.g. `"thermal:nickel_ore"`). Use when a material resolves to multiple ore blocks (stone/deepslate variants, cross-mod duplicates) and you want generation to use one specific block. Defaults to the material's whole `c:ores/<name>` tag. |
| `gen_raw` | No | Preferred raw item id for Prismatic Demonite drops (e.g. `"thermal:raw_nickel"`). Use when a material's `raw_tag` resolves to multiple items and you want drops to use one specific item. Defaults to every item in the material's `raw_tag`. |

### Auto-Generated Content

For each material, the system automatically generates:

- **Items**. Fragment, gravel, and/or dust items with color-tinted base textures.
- **Tags**. `c:fragments/{name}`, `c:gravels/{name}`, `c:dusts/{name}`.
- **Smelting / blasting recipes**. Dust to output item (if `smelt_to` is defined).
- **Athanor recipes**. Full ore processing chain (ore/raw -> fragments -> gravel -> dust).
- **Tabula Vitae recipes**. Ore to dust, fragments to gravel with corrupted dust.
- **Item models and translations**. Generated in-memory, no files needed.

All recipes use `neoforge:item_exists` conditions so they silently disable if the output item's mod is not installed.

### Ore Generation Injection

Any material flagged `generative` (the default) is automatically woven into NeoVitae's world-generation and reward systems. No recipe or tag authoring is required; a pack that adds platinum, mithril, or any ore the material system detects gets it everywhere for free.

- **Prismatic Demonite drops**. Mining Prismatic Demonite in the dungeon drops a random raw material drawn from the `raw_tag` of every generative material.
- **Dungeon iron-ore deposits**. The hand-placed ore in dungeon rooms (armoury, ore cavern, crane) is swapped at placement time for a random spread of generative ores.
- **Meteor ritual**. The default meteors include a weighted entry pointing at the generative ore set, so summoned meteors carry the pack's ores.

These three systems all read the auto-populated `neovitae:generative_ores` block tag, which lists the ore blocks of every generative material. Ores that don't exist in the pack contribute nothing.

To pull a single ore out of all of this, set `"generative": false` on its material entry. When a material maps to more than one candidate (a stone and deepslate variant, or duplicate ores/raw items from several mods), pin the exact output: `gen_ore` chooses the block used by the block-placing systems (dungeon deposits, meteors) and `gen_raw` chooses the raw item used by Prismatic Demonite drops. Without them, generation uses the material's whole `c:ores/<name>` tag and `raw_tag` respectively.

### Auto-Discovery Command

**`/neovitae generate-materials`**. Requires op permissions.

Scans all `c:ores/*` tags from installed mods and:

1. Discovers ore materials not already in the config.
2. Looks up smelting recipes to find the output item.
3. Falls back to `c:ingots/{name}` or `c:gems/{name}` tag matching.
4. Extracts the ore's characteristic color from its block texture (filtering out stone-colored pixels).
5. Appends new material entries to the config.

A game restart is required after running the command for new items to appear.

### First-Run Auto-Discovery

On the very first launch (when `materials.json` does not exist), the mod automatically runs the ore discovery process after the world loads. Any detected ores are added to the config and a notification is sent to players on join indicating a restart is needed.

### Adding a Custom Material Manually

1. Open `config/neovitae/materials.json`.
2. Add a new entry to the JSON array.
3. Restart the game.
4. The new material's items, models, tags, and recipes appear automatically.

### Removing a Material

Remove the entry from `materials.json` and restart. Existing items in the world will become unknown items.

---

## KubeJS Event Hooks

NeoVitae fires events that can be intercepted with KubeJS to add custom behavior, modify outputs, or cancel operations. Events live on `NeoForge.EVENT_BUS`.

### Ritual Events

```js
// server_scripts/blood_magic_rituals.js

// Cancel or modify ritual activation
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.RitualEvent$Activate', event => {
    const ritual = event.getRitual()
    const player = event.getPlayer()
    const pos = event.getPos()

    // Require player to be at night for certain rituals
    if (ritual.getName() === 'neovitae:night_ritual' && event.getLevel().isDay()) {
        player.displayClientMessage(Component.literal('This ritual can only be performed at night!'), true)
        event.setCanceled(true)
        return
    }

    console.log(`Player ${player.getName().getString()} activated ritual at ${pos}`)
})

// React after ritual activation (cannot cancel)
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.RitualEvent$Activated', event => {
    const ritual = event.getRitual()
    const player = event.getPlayer()
    // Grant advancement, award, etc.
})

// Cancel individual ritual performance ticks
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.RitualEvent$Perform', event => {
    const ritual = event.getRitual()
    const level = event.getLevel()

    // Pause ritual during rain
    if (level.isRaining() && ritual.getName() === 'neovitae:sun_ritual') {
        event.setCanceled(true)
    }
})

// React when ritual stops
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.RitualEvent$Stop', event => {
    const breakType = event.getBreakType()
    console.log(`Ritual stopped: ${breakType}`)
})
```

### Imperfect Ritual Events

```js
// server_scripts/blood_magic_imperfect.js

NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.ImperfectRitualEvent$Activate', event => {
    const ritual = event.getRitual()
    const player = event.getPlayer()
    const cost = event.getActivationCost()

    // Require specific item in hand
    if (ritual.getName() === 'neovitae:special_ritual') {
        const mainHand = player.getMainHandItem()
        if (!mainHand.is('minecraft:nether_star')) {
            player.displayClientMessage(Component.literal('You need a Nether Star!'), true)
            event.setCanceled(true)
        }
    }
})

NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.ImperfectRitualEvent$Activated', event => {
    const ritual = event.getRitual()
    const player = event.getPlayer()
    const pos = event.getPos()
    // Spawn particles, play sounds, log for analytics, etc.
})
```

### Ara Vitae Craft Events

```js
// server_scripts/blood_magic_altar.js

NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.AraVitaeCraftEvent$Crafting', event => {
    const input = event.getInput()
    const output = event.getOutput()
    const tier = event.getTier()

    // Chance for bonus output at higher tiers
    if (tier >= 3 && Math.random() < 0.1) {
        const bonusOutput = output.copy()
        bonusOutput.setCount(output.getCount() * 2)
        event.setOutput(bonusOutput)
    }
})

NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.AraVitaeCraftEvent$Crafted', event => {
    const output = event.getOutput()
    console.log(`Crafted: ${output.getId()}`)
})
```

### Event Reference

| Event Class | Cancellable | Description |
|------------|-------------|-------------|
| `RitualEvent$Activate` | Yes | Before ritual activates |
| `RitualEvent$Activated` | No | After ritual activated |
| `RitualEvent$Perform` | Yes | Before each ritual tick |
| `RitualEvent$Stop` | No | When ritual stops |
| `ImperfectRitualEvent$Activate` | Yes | Before imperfect ritual |
| `ImperfectRitualEvent$Activated` | No | After imperfect ritual |
| `AraVitaeCraftEvent$Crafting` | Yes | Before altar craft completes |
| `AraVitaeCraftEvent$Crafted` | No | After altar craft completes |
| `ItemBindEvent` | Yes | When binding item to player |
| `LaminaMaleficusEvent` | Yes | When dagger drains health |
| `SentientArmourEvent` | Varies | Sentient armor upgrade events |
| `AlchemyArrayCraftEvent` | Yes | Alchemy array crafting |

### Why Events Instead of Custom Ritual Types?

1. **No Java required**. All customization via KubeJS scripts.
2. **Hot-reloadable**. Use `/kubejs reload` to test changes instantly.
3. **Composable**. Add multiple behaviors to existing rituals.
4. **Safe**. Cannot break core mod functionality.
5. **Flexible**. Combine with other KubeJS features (quests, rewards, etc.).

For truly new ritual types (new effects, new multiblock structures), see the [API Overview](API-Overview).

---

## Custom Dungeon Rooms

The [Endless Dungeon](The-Endless-Dungeon) is assembled from a registry of **dungeon rooms**, each composed of one or more NBT structures plus a JSON definition. Pack makers can add rooms by dropping the NBT and JSON in the right places and registering the room id with a Java addon.

### File Layout

NeoVitae's room data lives under `assets/<namespace>/schematics/` (not `data/`). NBT structures live under `data/<namespace>/structure/`. Two parallel directory trees:

```
data/neovitae/structure/                    # NBT structures placed in-world
├── four_way_corridor.nbt
├── straight_corridor.nbt
├── t3_entrance.nbt
├── mini_dungeon/
│   ├── armoury.nbt
│   ├── crypt.nbt
│   ├── farm.nbt
│   ├── library.nbt
│   └── portal_nether.nbt
├── mines/
│   └── mine_key.nbt
├── standard/                               # full-size rooms (challenge tower, etc.)
└── ritual/                                 # ritual rooms

assets/neovitae/schematics/                 # JSON room definitions
├── four_way_corridor.json
├── t3_entrance.json
├── mini_dungeon/...
├── standard/...
├── mines/...
└── room_pools/                             # pool lists (weight;room_id)
    ├── connective_corridors.json
    ├── tier1/mini_dungeon.json
    ├── standard/standard_rooms.json
    └── special/mine_entrances.json
```

When loaded, a room definition is resolved by `ResourceLocation` (e.g. `neovitae:four_way_corridor`) and its JSON is fetched from `/assets/<namespace>/schematics/<path>.json`. The NBT structures it references live at `data/<namespace>/structure/<path>.nbt`.

### Room Definition Schema

A room definition is a single JSON object with these fields (output of `DungeonRoomProvider` in the NeoVitae repo):

| Field | Type | Description |
|-------|------|-------------|
| `structureMap` | `{ "<namespace:path>": {x,y,z} }` | One or more NBT structures and their placement offsets relative to the room origin. Multiple structures can share an offset; the synthesizer picks one at random per generation. |
| `descriptorList` | Array of `{minimumOffset, maximumOffset}` | One or more AABB regions describing the room's collision footprint. Used to reject overlap with other rooms. |
| `doorMap` | `{ "<doorType>": { "<facing>": [{x,y,z}, ...] } }` | Door positions grouped by door type (`default`, `mine`, custom) and facing direction (`north`, `south`, `east`, `west`). |
| `indexToDoorMap` | `{ "<index>": [{x,y,z}, ...] }` | Groups doors into numbered indices. Doors in the same index share the same connected-room-pool list. |
| `indexToRoomTypeMap` | `{ "<index>": ["<roomPoolId>" or "#<id>" or "$<id>"] }` | For each index, the list of room pools eligible to attach. Prefix `#` = special pool, `$` = deadend pool, no prefix = normal pool. |
| `requiredDoorMap` | `{ "<wantedDoorType>": [{x,y,z}, ...] }` | When a door at a position needs the connected room to expose a specific door type (used by waterway/asymmetric connectors). |
| `doorCoverMap` | `{ "<index>": {minimumOffset, maximumOffset} }` | Per-index AABB of blocks filled in when a door is sealed (no room connects). Default is a 3×3×1 frame in front of the door. |
| `dungeonWeight` | Integer | Spawn weight in the parent pool. Default 1. |
| `oreDensity` | Float | 0.0 - 1.0. Fraction of raw dungeon stone in the room converted to Dungeon Ore (drops raw demonite) or, more rarely, Prismatic Demonite. |
| `spawnLocation` | `{x,y,z}` | Player spawn offset (entrance rooms only). |
| `controllerOffset` | `{x,y,z}` | Dungeon-controller block offset (entrance rooms only). |
| `portalOffset` | `{x,y,z}` | Portal/exit offset (entrance rooms only). |

### Example: A Four-Way Corridor

This is the actual `assets/neovitae/schematics/four_way_corridor.json` ships in NeoVitae:

```json
{
  "controllerOffset": { "x": 0, "y": 0, "z": 0 },
  "descriptorList": [
    {
      "minimumOffset": { "x": 0, "y": 0, "z": 0 },
      "maximumOffset": { "x": 11, "y": 6, "z": 11 }
    }
  ],
  "doorCoverMap": {},
  "doorMap": {
    "default": {
      "north": [ { "x": 5, "y": 0, "z": 0 } ],
      "south": [ { "x": 5, "y": 0, "z": 10 } ],
      "west":  [ { "x": 0, "y": 0, "z": 5 } ],
      "east":  [ { "x": 10, "y": 0, "z": 5 } ]
    }
  },
  "dungeonWeight": 1,
  "indexToDoorMap": {
    "1": [
      { "x": 5, "y": 0, "z": 0 },
      { "x": 5, "y": 0, "z": 10 },
      { "x": 0, "y": 0, "z": 5 },
      { "x": 10, "y": 0, "z": 5 }
    ]
  },
  "indexToRoomTypeMap": {
    "1": [
      "neovitae:room_pools/standard/standard_rooms",
      "neovitae:room_pools/connective_corridors",
      "#neovitae:room_pools/special/mine_entrances",
      "#neovitae:room_pools/standard/mine_key"
    ]
  },
  "oreDensity": 0.0,
  "portalOffset": { "x": 0, "y": 0, "z": 0 },
  "requiredDoorMap": {},
  "spawnLocation": { "x": 0, "y": 0, "z": 0 },
  "structureMap": {
    "neovitae:four_way_corridor": { "x": 0, "y": 0, "z": 0 }
  }
}
```

All four doors share index `1`, which connects to a mix of standard rooms, connective corridors, and special pools.

### Room Pools

A room pool is a JSON list of `"weight;namespace:room_id"` strings, served from `assets/<namespace>/schematics/room_pools/<path>.json`:

```json
[
  "2;neovitae:four_way_corridor",
  "3;neovitae:overlapped_corridor",
  "4;neovitae:straight_corridor",
  "2;neovitae:t_corridor"
]
```

When the synthesizer needs to attach a room to a door, it picks an entry weighted-random from the named pool, rotates it to match the door facing, and validates that none of its `descriptorList` AABBs overlap an existing placed room.

The shipped pools cover:

| Pool | Used by |
|------|---------|
| `room_pools/connective_corridors` | Doors that prefer hallways |
| `room_pools/standard/standard_rooms` | Standard dungeon room slots |
| `room_pools/tier1/mini_dungeon` | T3 entrance interior rooms |
| `room_pools/standard/standard_deadend` | Deadend fillers for standard tier |
| `room_pools/mines/*` | Mine-tier rooms, corridors, entrances |
| `room_pools/special/mine_entrances` | Doors that may upgrade to a mine entrance |
| `room_pools/standard/mine_key` | Mine key special rooms |

### Authoring a New Room (Datapack Side)

1. **Build the room** in a creative test world. Use a Structure Block to save it as an NBT file.
2. **Drop the NBT** into `data/<your_namespace>/structure/<path>.nbt`. The path you choose here is the same path you'll use in `structureMap`.
3. **Author the JSON definition** at `assets/<your_namespace>/schematics/<room_id>.json` using the schema above.
   - Place door positions at the **outside edge** of where the door block sits (the door cover AABB will fill in around it).
   - Set `descriptorList` to fully enclose the structure footprint plus any overhangs.
   - For irregular rooms (e.g. an L-shape), use multiple rectangles in `descriptorList`.
4. **Add the room to a pool** by appending its weighted id to the relevant pool file under `assets/<your_namespace>/schematics/room_pools/`.
5. **Register the id**: a small Java addon needs to call `DungeonRoomRegistry.registerUnloadedDungeonRoom(new ResourceLocation("your_namespace", "your_room_id"))` during mod setup. Pure-datapack rooms can be added by overriding NeoVitae's existing pool JSONs and reusing room ids already registered.

### Tips

- **Reference NeoVitae's generated JSONs** in the repo at `src/generated/resources/assets/neovitae/schematics/`. They cover every door arrangement (single door, multi-level, asymmetric waterway connectors, multi-NBT rooms) and are the authoritative examples.
- **Indices group doors** that should accept the same neighbour pool. A four-way corridor with one index attaches anything to any door; a station with `index: 1` north/south for mine corridors and `index: 2` east for a side passage attaches different pools per direction.
- **Ore density is per-room**: the fraction of raw dungeon stone converted to Dungeon Ore (and occasionally Prismatic Demonite). Mines use 0.2-0.4, ore-cavern rooms 0.6, mine-key/deadend rooms 0.8.
- **`/neovitae dungeon-showcase`** places every registered structure NBT in a grid for visual review. Use this to verify your NBT loads correctly before wiring up the JSON definition.

---

## Telepose Blacklist

NeoVitae exposes two `telepose_blacklist` tags that prevent the Teleposer from moving blocks or entities. See [Teleposer](Teleposer) for the player-facing tool.

### Block Tag

**Location:** `data/neovitae/tags/block/telepose_blacklist.json`

Blocks in this tag cannot be teleposed. Useful for protecting bedrock-equivalents, modded "cannot be moved" blocks, or pack-defined infrastructure.

```json
{
  "replace": false,
  "values": [
    "minecraft:bedrock",
    "minecraft:end_portal_frame",
    "some_mod:reactor_core"
  ]
}
```

### Entity Tag

**Location:** `data/neovitae/tags/entity_type/telepose_blacklist.json`

Entities in this tag cannot be teleposed. Useful for bosses, quest NPCs, and anything that should stay put.

```json
{
  "replace": false,
  "values": [
    "minecraft:wither",
    "minecraft:ender_dragon",
    "some_mod:quest_villager"
  ]
}
```

Both tags resolve at telepose time; changes take effect after `/reload`.
