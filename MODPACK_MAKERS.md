# Neo Vitae - Modpack Maker's Guide

This guide covers all the data-driven systems in Neo Vitae that modpack makers can customize via datapacks. No Java code required!

## Table of Contents

1. [Getting Started](#getting-started)
2. [DataMaps (Primary Customization)](#datamaps-primary-customization)
   - [Blood Orb Stats](#blood-orb-stats)
   - [Sigil Stats](#sigil-stats)
   - [Ritual Stats](#ritual-stats)
   - [Imperfect Ritual Stats](#imperfect-ritual-stats)
   - [Spiritus Gem Capacities](#spiritus-gem-capacities)
   - [Dungeon Ore Weights](#dungeon-ore-weights)
3. [Sigil Types (Effect Definitions)](#sigil-types-effect-definitions)
4. [Recipe Types](#recipe-types)
5. [Tags](#tags)
6. [Loot Tables & Modifiers](#loot-tables--modifiers)
7. [Living Armor Upgrades](#living-armor-upgrades)
8. [Curios Integration](#curios-integration)
9. [KubeJS Event Hooks](#kubejs-event-hooks)
10. [Custom Player Attributes](#custom-player-attributes)
11. [Examples](#examples)

---

## Getting Started

Neo Vitae uses NeoForge's DataMap system for most customizations. To customize the mod:

1. Create a datapack in your modpack's `datapacks/` folder or use KubeJS
2. Create the appropriate directory structure under `data/neovitae/`
3. Add or modify JSON files as documented below

**Directory Structure:**

```
your_datapack/
├── pack.mcmeta
└── data/
    └── neovitae/
        ├── data_maps/
        │   ├── block/
        │   │   └── dungeon_ore_weights.json
        │   ├── item/
        │   │   ├── blood_orb_stats.json
        │   │   ├── sigil_stats.json
        │   │   └── spiritus_gem_max.json
        │   └── neovitae/
        │       ├── ritual/
        │       │   └── ritual_stats.json
        │       └── imperfect_ritual/
        │           └── imperfect_ritual_stats.json
        ├── recipes/
        ├── tags/
        └── loot_table/
```

---

## DataMaps (Primary Customization)

DataMaps are NeoForge's system for attaching data to registry entries. Neo Vitae uses them extensively for balancing.

### Blood Orb Stats

**Location:** `data/neovitae/data_maps/item/blood_orb_stats.json`

Customize the tier, capacity, and fill rate of blood orbs.

| Field | Type | Description |
|-------|------|-------------|
| `tier` | Integer | Orb tier (0-5). Determines recipe requirements. |
| `capacity` | Integer | Maximum LP the orb can store in the soul network |
| `fillRate` | Integer | LP gained per tick when draining health |

**Example - Make Weak Orb hold more LP:**

```json
{
  "values": {
    "neovitae:weak_blood_orb": {
      "capacity": 10000,
      "fillRate": 3,
      "tier": 0
    }
  }
}
```

**Default Values:**

| Orb | Tier | Capacity | Fill Rate |
|-----|------|----------|-----------|
| Weak | 0 | 5,000 | 2 |
| Apprentice | 1 | 25,000 | 5 |
| Magician | 2 | 150,000 | 15 |
| Master | 3 | 1,000,000 | 25 |
| Archmage | 4 | 10,000,000 | 50 |

### Blood Orb Internal Fluid Tank

Each Blood Orb has an internal fluid reservoir that stores Essentia Vitae. The capacity of this reservoir is calculated as `4000 + (tier * 2000)` mB. When the altar fills the player's Anima (soul network), it also fills the orb's internal tank with the same amount.

**Dual-Mode Altar Behavior:**

When a Blood Orb is placed in an Ara Vitae, the altar checks whether the orb's internal tank contains fluid:

1. **Orb has fluid (draining mode):** The altar drains the orb's internal tank at 10x the orb's normal fill rate. If the altar basin has at least 1,000 mB of room, the drained fluid goes into the altar. If the altar is nearly full (less than 1,000 mB of room), the drained fluid is channeled into the player's Anima instead.

2. **Orb is empty (normal mode):** The altar operates normally, draining its own LP into the player's Anima at the orb's standard fill rate.

This means players can pre-fill orbs with Essentia Vitae (for example, from fluid pipes or the Athanor) and then use those orbs to rapidly refill an altar or top off their network. The 10x transfer rate makes this significantly faster than normal altar filling.

**Debug Command:** Use `/neovitae setorbfill <amount>` (or `/nvsetorbfill <amount>`) to manually set the fluid amount in a held Blood Orb for testing purposes. Requires operator permissions.

---

### Sigil Stats

**Location:** `data/neovitae/data_maps/item/sigil_stats.json`

Customize LP costs and effect parameters for all sigils.

| Field | Type | Description |
|-------|------|-------------|
| `lp_cost` | Integer | LP cost per activation |
| `drain_interval` | Integer | Ticks between LP drains for toggleable sigils (default: 100 = 5 sec) |
| `range` | Integer | Horizontal radius for area effects (optional) |
| `vertical_range` | Integer | Vertical range for area effects (optional) |
| `effect_duration` | Integer | Duration in ticks for potion effects (optional) |
| `effect_level` | Integer | Potion effect amplifier level (optional) |

**Example - Reduce Air Sigil cost and make Fast Miner cheaper:**

```json
{
  "values": {
    "neovitae:air_sigil": {
      "lp_cost": 25
    },
    "neovitae:fast_miner_sigil": {
      "lp_cost": 50,
      "drain_interval": 200,
      "range": 15,
      "effect_duration": 1200,
      "effect_level": 3
    }
  }
}
```

**Default Sigil Costs:**

| Sigil | LP Cost | Notes |
|-------|---------|-------|
| Air | 50 | Per use |
| Water | 100 | Per use |
| Lava | 1,000 | Per use |
| Void | 50 | Per use |
| Blood Light | 10 | Per use |
| Teleposition | 1,000 | Per use |
| Divination | 0 | Free |
| Seer | 0 | Free |
| Fast Miner | 100 | Toggleable, range: 10 |
| Green Grove | 150 | Toggleable, range: 3 |
| Magnetism | 50 | Toggleable, range: 5 |
| Frost | 100 | Toggleable, range: 2 |
| Suppression | 400 | Toggleable, range: 5 |
| Phantom Bridge | 100 | Toggleable |

---

### Ritual Stats

**Location:** `data/neovitae/data_maps/neovitae/ritual/ritual_stats.json`

Customize activation costs, refresh costs, and range limits for rituals.

| Field | Type | Description |
|-------|------|-------------|
| `activation_cost` | Integer | LP cost to activate the ritual |
| `refresh_cost` | Integer | LP cost per refresh tick |
| `refresh_time` | Integer | Ticks between refreshes (default: 20 = 1 sec) |
| `crystal_level` | Integer | Required activation crystal (0=weak, 1=awakened, 2=creative) |
| `range_limits` | Object | Map of range names to limit objects |
| `enabled` | Boolean | Whether the ritual is enabled (default: true). Disabled rituals cannot be activated and are hidden from JEI. |

**Range Limit Object:**

```json
{
  "maxVolume": 1000,
  "maxHorizontalRadius": 10,
  "maxVerticalRadius": 10
}
```

**Example - Make Water Ritual cheaper and faster:**

```json
{
  "values": {
    "neovitae:water": {
      "activation_cost": 250,
      "refresh_cost": 10,
      "refresh_time": 10,
      "crystal_level": 0
    }
  }
}
```

**Example - Customize Well of Suffering range:**

```json
{
  "values": {
    "neovitae:suffering": {
      "activation_cost": 50000,
      "refresh_cost": 2,
      "refresh_time": 25,
      "crystal_level": 0,
      "range_limits": {
        "damage": {
          "maxVolume": 2000,
          "maxHorizontalRadius": 15,
          "maxVerticalRadius": 15
        }
      }
    }
  }
}
```

---

### Imperfect Ritual Stats

**Location:** `data/neovitae/data_maps/neovitae/imperfect_ritual/imperfect_ritual_stats.json`

Customize imperfect rituals - simple one-time effects triggered by placing a block on an imperfect ritual stone.

| Field | Type | Description |
|-------|------|-------------|
| `activation_cost` | Integer | LP cost for activation |
| `block` | String | Block registry name (e.g., "minecraft:water") |
| `block_tag` | String | Alternative: use a block tag instead |
| `consume_block` | Boolean | Whether the catalyst block is consumed (default: false) |
| `lightning_effect` | Boolean | Whether lightning strikes on activation (default: true) |
| `enabled` | Boolean | Whether the ritual is enabled (default: true). Disabled rituals cannot be activated and are hidden from JEI. |

**Example - Custom imperfect ritual:**

```json
{
  "values": {
    "neovitae:rain": {
      "activation_cost": 2500,
      "block": "minecraft:water",
      "consume_block": false,
      "lightning_effect": false
    }
  }
}
```

**Default Imperfect Rituals:**

| Ritual | Block | Cost | Consumes | Lightning |
|--------|-------|------|----------|-----------|
| Rain | water | 5,000 | No | No |
| Zombie Resurrection | coal_block | 5,000 | No | Yes |
| Resistance | bedrock | 5,000 | No | No |

---

### Spiritus Gem Capacities

**Location:** `data/neovitae/data_maps/item/spiritus_gem_max.json`

Customize how much Demon Will each soul gem tier can hold.

**Example:**

```json
{
  "values": {
    "neovitae:soul_gem_petty": 128,
    "neovitae:soul_gem_lesser": 512,
    "neovitae:soul_gem_common": 2048,
    "neovitae:soul_gem_greater": 8192,
    "neovitae:soul_gem_grand": 32768
  }
}
```

**Default Capacities:**

| Gem | Capacity |
|-----|----------|
| Petty | 64 |
| Lesser | 256 |
| Common | 1,024 |
| Greater | 4,096 |
| Grand | 16,384 |

### Dungeon Ore Weights

**Location:** `data/neovitae/data_maps/block/dungeon_ore_weights.json`

Controls which ores spawn in dungeon rooms when stone blocks are replaced. Each entry maps a block to an integer weight; higher weight means more frequent. Pack makers can add modded ores or adjust the distribution.

**Example - Add modded ores:**

```json
{
  "values": {
    "mekanism:tin_ore": 20,
    "mekanism:osmium_ore": 15,
    "mekanism:uranium_ore": 3
  }
}
```

**Default Distribution:**

| Block | Weight | ~Chance |
|-------|--------|---------|
| `minecraft:coal_ore` | 40 | 28% |
| `minecraft:iron_ore` | 30 | 21% |
| `minecraft:copper_ore` | 25 | 17% |
| `minecraft:gold_ore` | 15 | 10% |
| `minecraft:redstone_ore` | 15 | 10% |
| `minecraft:lapis_ore` | 10 | 7% |
| `minecraft:diamond_ore` | 5 | 3.5% |
| `minecraft:emerald_ore` | 3 | 2% |

Ore density per room is set by the room definition (corridors ~20%, standard rooms ~40%, mine key/deadend rooms ~80%). The weights only control which ore is chosen when a stone block is replaced.

---

## Sigil Types (Effect Definitions)

**Location:** `data/neovitae/neovitae/sigil_type/`

Sigil types define the behavior of sigils using a codec-based effect system. Each sigil has a JSON file defining its effect type and parameters.

**Structure:**

```json
{
  "effect": {
    "type": "neovitae:effect_type",
    // Effect-specific parameters
  },
  "lp_cost_air": 50,
  "lp_cost_block": 50,
  "toggleable": false,
  "drain_interval": 100
}
```

**Built-in Effect Types:**

- `neovitae:air` - Launch player into air
- `neovitae:water` - Place water source
- `neovitae:lava` - Place lava source
- `neovitae:void` - Remove fluids
- `neovitae:blood_light` - Create light source
- `neovitae:divination` - Show altar/network info
- `neovitae:teleposition` - Teleport to bound location
- `neovitae:fast_miner` - Haste effect
- `neovitae:green_grove` - Accelerate growth
- `neovitae:magnetism` - Pull items
- `neovitae:frost` - Freeze water
- `neovitae:suppression` - Push away fluids
- `neovitae:phantom_bridge` - Create phantom blocks

---

## Recipe Types

Neo Vitae adds several recipe types that can be customized via datapacks.

### Ara Vitae Recipes

**Location:** `data/neovitae/recipes/altar/`

```json
{
  "type": "neovitae:ara_vitae",
  "ingredient": {
    "item": "minecraft:diamond"
  },
  "result": {
    "id": "neovitae:weak_blood_shard"
  },
  "minTier": 3,
  "totalBlood": 10000,
  "craftSpeed": 100,
  "drainSpeed": 50
}
```

| Field | Description |
|-------|-------------|
| `ingredient` | Input item (standard ingredient format) |
| `result` | Output item stack |
| `minTier` | Minimum altar tier required (0-5) |
| `totalBlood` | Total LP required for crafting |
| `craftSpeed` | LP consumed per craft tick |
| `drainSpeed` | Max LP drained from altar per tick |

### Hellfire Forge Recipes

**Location:** `data/neovitae/recipes/hellfire_forge/`

```json
{
  "type": "neovitae:hellfire_forge",
  "ingredients": [
    { "item": "minecraft:iron_ingot" },
    { "item": "minecraft:redstone" }
  ],
  "result": {
    "id": "neovitae:soul_snare",
    "count": 4
  },
  "minimumSouls": 64,
  "soulDrain": 16
}
```

### Tabula Vitae Recipes

**Location:** `data/neovitae/recipes/tabula_vitae/`

```json
{
  "type": "neovitae:tabula_vitae",
  "ingredients": [
    { "item": "minecraft:glass_bottle" },
    { "item": "neovitae:reagent_water" }
  ],
  "result": {
    "id": "minecraft:potion",
    "components": { "potion": "minecraft:water" }
  },
  "minTier": 1,
  "lpDrained": 100
}
```

### Athanor (ARC) Recipes

**Location:** `data/neovitae/recipes/athanor/`

```json
{
  "type": "neovitae:athanor",
  "input": { "item": "minecraft:iron_ore" },
  "tool": { "tag": "neovitae:athanor_tool/explosives" },
  "output": {
    "id": "neovitae:iron_fragment",
    "count": 3
  },
  "inputFluid": {
    "id": "minecraft:water",
    "amount": 100
  },
  "outputFluid": {
    "id": "minecraft:lava",
    "amount": 50
  },
  "addedOutput": [
    {
      "item": { "id": "neovitae:iron_fragment" },
      "chance": 0.5
    }
  ]
}
```

### Alchemy Array Recipes

**Location:** `data/neovitae/recipes/array/`

```json
{
  "type": "neovitae:alchemy_array",
  "base_input": { "item": "neovitae:arcane_ash" },
  "added_input": { "item": "minecraft:feather" },
  "result": { "id": "neovitae:air_sigil" }
}
```

### Meteor Recipes

**Location:** `data/neovitae/recipes/meteor/`

Define what blocks spawn when the Meteor ritual is activated.

---

## Tags

Tags control various gameplay mechanics. Override or extend these in your datapack.

### Block Tags

**Location:** `data/neovitae/tags/blocks/`

| Tag | Purpose |
|-----|---------|
| `altar/runes` | Blocks that count as altar runes |
| `altar/pillars` | Valid pillar blocks for altar tiers |
| `altar/t3_capstones` - `t6_capstones` | Tier-specific capstone blocks |
| `tranquility/plant` | Plant blocks for tranquility bonus |
| `tranquility/water` | Water blocks for tranquility |
| `tranquility/fire` | Fire/heat blocks for tranquility |
| `tranquility/earthen` | Earth blocks for tranquility |
| `incense_path/level_0` - `level_10` | Valid path blocks by distance from incense altar |
| `mundane_block` | Blocks deleted by Voiding anointment |

### Item Tags

**Location:** `data/neovitae/tags/items/`

| Tag | Purpose |
|-----|---------|
| `soul_gems` | Items that hold Demon Will |
| `athanor_tool` | Tools usable in the Athanor |
| `athanor_tool/explosives` | Explosive tools (ore doubling) |
| `athanor_tool/cutting_fluids` | Cutting tools |
| `athanor_tool/furnace` | Smelting tools |
| `crystals/demon` | Demon crystal items |
| `charges` | Explosive charges |
| `blood_mending_blacklist` | Items that cannot receive or benefit from Blood Mending |
| `spiritus_capable` | Items that can receive spiritus storage via Hellfire Forge infusion (armor, tools, weapons, shields) |
| `anointable/melee` | Items that can receive melee-category anointments |
| `anointable/mining` | Items that can receive mining-category anointments |
| `anointable/bows` | Items that can receive bow-category anointments |
| `anointable/weapons` | Items that can receive any-weapon anointments (parent of melee + bows) |

### Anointable Item Tags

Each anointment is restricted to a specific item tag. If a tool's id is not in the matching tag, the anointment right-click (or smithing table apply) silently does nothing. Add your modded weapons, tools, or custom bows to these tags to make them valid targets.

**Built-in assignments** (source: `AnointmentRegistrar.java`):

| Anointment | Tag |
|------------|-----|
| Honing Oil (Melee Damage) | `neovitae:anointable/melee` |
| Holy Water | `neovitae:anointable/melee` |
| Plunderer's Glint (Looting) | `neovitae:anointable/melee` |
| Soft Coating (Silk Touch) | `neovitae:anointable/mining` |
| Fortuna Extract (Fortune) | `neovitae:anointable/mining` |
| Miner's Secrets (Hidden Knowledge) | `neovitae:anointable/mining` |
| Slow-burning Oil (Smelting) | `neovitae:anointable/mining` |
| Void Essence (Voiding) | `neovitae:anointable/mining` |
| Iron Tip (Bow Power) | `neovitae:anointable/bows` |
| Archer's Polish (Bow Velocity) | `neovitae:anointable/bows` |
| Dexterity Alkahest (Quick Draw) | `neovitae:anointable/bows` |
| Will Power | `neovitae:anointable/weapons` |
| Repairing Salve (Weapon Repair) | `neovitae:anointable/weapons` |

**Default tag members** (vanilla + NeoVitae):

| Tag | Contents |
|-----|----------|
| `anointable/melee` | `#minecraft:swords`, `#minecraft:axes` |
| `anointable/mining` | `#minecraft:pickaxes`, `#minecraft:shovels`, `#minecraft:axes` |
| `anointable/bows` | `minecraft:bow`, `minecraft:crossbow` |
| `anointable/weapons` | `#neovitae:anointable/melee`, `#neovitae:anointable/bows` |

**Adding a modded bow to Iron Tip:**

```json
// data/neovitae/tags/items/anointable/bows.json
{
  "replace": false,
  "values": [
    "tetra:modular_bow",
    "some_mod:magic_bow"
  ]
}
```

**Creating a one-off tag for a single anointment:** if you want to give one specific anointment its own applicability list (for example, a custom pack that restricts Honing Oil to only Netherite swords), override the tag or make a new one and point the anointment at it via a resource override. The `appliesTo(...)` call in `AnointmentRegistrar` accepts any `TagKey<Item>`, so in-code modders can declare their own tag like `neovitae:anointable/iron_tip_only` and wire the bow power anointment at it.

### Entity Tags

**Location:** `data/neovitae/tags/entity_types/`

| Tag | Purpose |
|-----|---------|
| `telepose_blacklist` | Entities that cannot be teleposed |
| `well_of_suffering_blacklist` | Entities immune to Well of Suffering ritual |
| `ritual_boss_blacklist` | Entities immune to ritual boss mechanics |
| `no_sacrifice` | Entities that provide no EV when killed (e.g., summoned undead servants) |

---

## Loot Tables & Modifiers

### Global Loot Modifiers

**Location:** `data/neovitae/loot_modifiers/`

Neo Vitae includes loot modifiers for anointments:

| Modifier | Effect |
|----------|--------|
| `smelting.json` | Auto-smelts drops (Smelting anointment) |
| `voiding.json` | Voids mundane blocks (Voiding anointment) |

**Note:** Silk Touch, Fortune, and Looting anointments are handled via NeoForge's `GetEnchantmentLevelEvent`, making them behave like real enchantments. This ensures proper compatibility with all blocks (including shulker boxes and other containers) and allows Fortune/Looting to stack with existing enchantments.

### Dungeon Loot Tables

**Location:** `data/neovitae/loot_table/chests/mines/`

Customize dungeon chest contents:

- `decent_loot.json` - General good items
- `food_loot.json` - Food supplies
- `mine_key_loot.json` - Keys and special items
- `ore_loot.json` - Ore materials
- `smithy_loot.json` - Crafting materials

---

## Living Armor Upgrades

Living armor upgrades are defined via datapack registries. Each upgrade has levels with XP requirements and effects.

**Location:** `data/neovitae/living_upgrade/`

Upgrades use effect components:

- Attribute modifiers (speed, damage, health, etc.)
- Status effects (fire resistance, etc.)
- Special behaviors (repair, elytra flight, etc.)

### Upgrade Tags

Control upgrade behavior with tags:

| Tag | Purpose |
|-----|---------|
| `living/trainers` | Upgrades that can gain XP |
| `living/is_downgrade` | Negative upgrades |
| `living/is_scrappable` | Can be removed with scrapper |
| `living/tooltip_hide` | Hidden from tooltips |
| `living/living_blacklist` | Upgrades that cannot be applied to Living Armor |

**Example - Blacklist an upgrade from Living Armor:**

This is useful for modpack makers who want to prevent certain upgrades from being applied to Living Armor, such as overpowered custom upgrades or upgrades that conflict with other mods

Create `data/neovitae/tags/neovitae/living_upgrade/living_blacklist.json`:

```json
{
  "replace": false,
  "values": [
    "neovitae:some_upgrade_id"
  ]
}
```

---

## Curios Integration

**Location:** `data/neovitae/curios/`

### Player Slots

`entities/bmplayerslots.json` - Define curios slots for players

### Living Armor Socket

`slots/living_armour_socket.json` - Socket slots for living armor upgrades

---

## Examples

### Complete Example: Easier Early Game

Create `data/neovitae/data_maps/item/blood_orb_stats.json`:

```json
{
  "values": {
    "neovitae:weak_blood_orb": {
      "capacity": 15000,
      "fillRate": 5,
      "tier": 0
    },
    "neovitae:apprentice_blood_orb": {
      "capacity": 50000,
      "fillRate": 10,
      "tier": 1
    }
  }
}
```

Create `data/neovitae/data_maps/item/sigil_stats.json`:

```json
{
  "values": {
    "neovitae:air_sigil": { "lp_cost": 25 },
    "neovitae:water_sigil": { "lp_cost": 50 },
    "neovitae:divination_sigil": { "lp_cost": 0 }
  }
}
```

### Complete Example: Harder Rituals

Create `data/neovitae/data_maps/neovitae/ritual/ritual_stats.json`:

```json
{
  "values": {
    "neovitae:water": {
      "activation_cost": 1000,
      "refresh_cost": 50,
      "refresh_time": 40,
      "crystal_level": 0
    },
    "neovitae:suffering": {
      "activation_cost": 100000,
      "refresh_cost": 10,
      "refresh_time": 20,
      "crystal_level": 1
    }
  }
}
```

### Complete Example: Disable Specific Rituals

Disable rituals entirely by setting `enabled` to `false`. Disabled rituals cannot be activated and are automatically hidden from JEI.

Create `data/neovitae/data_maps/neovitae/ritual/ritual_stats.json`:

```json
{
  "values": {
    "neovitae:meteor": {
      "activation_cost": 1000000,
      "refresh_cost": 0,
      "enabled": false
    },
    "neovitae:armour_evolve": {
      "activation_cost": 200000,
      "refresh_cost": 0,
      "enabled": false
    }
  }
}
```

Similarly for imperfect rituals, create `data/neovitae/data_maps/neovitae/imperfect_ritual/imperfect_ritual_stats.json`:

```json
{
  "values": {
    "neovitae:rain": {
      "activation_cost": 5000,
      "block": "minecraft:water",
      "enabled": false
    }
  }
}
```

### Adding Custom Altar Rune Blocks

Create `data/neovitae/tags/blocks/altar/runes.json`:

```json
{
  "replace": false,
  "values": [
    "minecraft:crying_obsidian",
    "#forge:storage_blocks/amethyst"
  ]
}
```

---

## KubeJS Event Hooks

Neo Vitae fires events that can be intercepted with KubeJS to add custom behavior, modify outputs, or cancel operations.

### Ritual Events

**Location:** `NeoForge.EVENT_BUS`

Hook into ritual activation and performance to create custom ritual behaviors:

```javascript
// server_scripts/blood_magic_rituals.js

// Cancel or modify ritual activation
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.RitualEvent$Activate', event => {
    const ritual = event.getRitual()
    const player = event.getPlayer()
    const pos = event.getPos()

    // Example: Require player to be at night for certain rituals
    if (ritual.getName() === 'neovitae:night_ritual' && event.getLevel().isDay()) {
        player.displayClientMessage(Component.literal('This ritual can only be performed at night!'), true)
        event.setCanceled(true)
        return
    }

    // Example: Add custom side effects
    console.log(`Player ${player.getName().getString()} activated ritual at ${pos}`)
})

// React after ritual activation (cannot cancel)
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.RitualEvent$Activated', event => {
    const ritual = event.getRitual()
    const player = event.getPlayer()

    // Example: Grant advancement or award
    // player.server.runCommand(`advancement grant ${player.getName().getString()} only my_pack:ritual_master`)
})

// Cancel individual ritual performance ticks
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.RitualEvent$Perform', event => {
    const ritual = event.getRitual()
    const level = event.getLevel()

    // Example: Pause ritual during rain
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

Hook into imperfect (one-time) ritual activation, This can allow for complete custom effects or code to run with an imperfect ritual. You can do all kinds of neat stuff with this!

```javascript
// server_scripts/blood_magic_imperfect.js

// Cancel or modify imperfect ritual activation
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.ImperfectRitualEvent$Activate', event => {
    const ritual = event.getRitual()
    const player = event.getPlayer()
    const cost = event.getActivationCost()

    // Example: Double cost on hard difficulty
    if (player.level.getDifficulty().name() === 'HARD') {
        // Note: Cost modification would need to be handled differently
        // This is just showing you have access to the values
    }

    // Example: Require specific item in hand
    if (ritual.getName() === 'neovitae:special_ritual') {
        const mainHand = player.getMainHandItem()
        if (!mainHand.is('minecraft:nether_star')) {
            player.displayClientMessage(Component.literal('You need a Nether Star!'), true)
            event.setCanceled(true)
        }
    }
})

// React after imperfect ritual completes
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.ImperfectRitualEvent$Activated', event => {
    const ritual = event.getRitual()
    const player = event.getPlayer()
    const pos = event.getPos()

    // Example: Spawn particles or play sounds
    // Example: Log for analytics
    // Blow up the player (just kidding. well...)
})
```

### Ara Vitae Craft Events

Hook into altar crafting to modify outputs or add side effects:

```javascript
// server_scripts/blood_magic_altar.js

// Modify or cancel altar crafting
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.AraVitaeCraftEvent$Crafting', event => {
    const input = event.getInput()
    const output = event.getOutput()
    const tier = event.getTier()

    // Example: Chance for bonus output at higher tiers
    if (tier >= 3 && Math.random() < 0.1) {
        const bonusOutput = output.copy()
        bonusOutput.setCount(output.getCount() * 2)
        event.setOutput(bonusOutput)
    }

    // Example: Cancel craft if altar is missing specific nearby block
    // const level = event.getLevel()
    // const pos = event.getPos()
    // if (!level.getBlockState(pos.above()).is('minecraft:beacon')) {
    //     event.setCanceled(true)
    // }
})

// React after successful craft
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
| `LivingArmourEvent` | Varies | Living armor upgrade events |
| `AlchemyArrayCraftEvent` | Yes | Alchemy array crafting |

### Why Events Instead of Custom Ritual Types?

Neo Vitae's event system provides several advantages for modpack customization:

1. **No Java Required**: All customization via KubeJS scripts
2. **Hot-Reloadable**: Use `/kubejs reload` to test changes instantly
3. **Composable**: Add multiple behaviors to existing rituals
4. **Safe**: Cannot break core mod functionality
5. **Flexible**: Combine with other KubeJS features (quests, rewards, etc.)

For truly new ritual types (new effects, new multiblock structures), those require Java mods. But for 90% of modpack needs, events provide sufficient customization.

---

## Custom Player Attributes

Neo Vitae registers several custom player attributes that can be modified via equipment, effects, data packs, or addon mods using standard Minecraft attribute modifiers.

### Attribute Reference

| Attribute | Registry ID | Default | Max | Description |
|-----------|------------|---------|-----|-------------|
| Self Sacrifice Multiplier | `neovitae:player.self_sacrifice_multiplier` | 1.0 | 100.0 | Multiplier for LP gained from self-sacrifice (PercentageAttribute) |
| Bonus Sacrifice | `neovitae:bonus_sacrifice` | 0.0 | 1000.0 | % bonus to LP gained from Lamina Exhauriens mob kills |
| Bonus Self Sacrifice | `neovitae:bonus_self_sacrifice` | 0.0 | 1000.0 | % bonus to LP gained from Lamina Maleficus self-sacrifice |
| Bonus Demon Will | `neovitae:bonus_demon_will` | 0.0 | 1000.0 | % bonus to Demon Will drops from sentient weapons and soul snares |
| Sigil Cost Reduction | `neovitae:sigil_cost_reduction` | 0.0 | 100.0 | % reduction to all sigil LP costs (capped at near-zero, minimum 1 LP) |
| Blood Siphon | `neovitae:blood_siphon` | 0.0 | 1024.0 | Converts damage dealt into LP. Base LP = min(attribute, damage), then multiplied |
| Blood Shield | `neovitae:blood_shield` | 0.0 | 10.0 | Reduces incoming damage by 10% per point (capped at 99%), drains LP for prevented damage |

### Blood Siphon Details

When a player with Blood Siphon deals damage:
- **LP gained** = min(blood_siphon_value, damage_dealt) x multiplier
- **vs Players (PvP)**: LP is drained directly from the target player's soul network and added to the attacker's. This is a true LP transfer — the victim loses the same amount the attacker gains. Multiplier = configurable (default: 100)
- **vs Mobs (PvE)**: LP is generated from nothing and added to the attacker's network. Multiplier = configurable (default: 10)
- Example (PvE): Blood Siphon 5, deal 10 damage to a mob = 5 x 10 = 50 LP gained
- Example (PvP): Blood Siphon 5, deal 10 damage to a player = 5 x 100 = 500 LP stolen from their network

### Blood Shield Details

When a player with Blood Shield takes damage:
- **Damage reduction** = 10% per attribute point (e.g., Blood Shield 5 = 50% reduction)
- **Hard cap**: 99% maximum reduction (at Blood Shield 10)
- **LP cost** = damage_prevented x configurable multiplier (default: 100)
- If insufficient LP: partial shield uses available LP, remaining damage passes through
- Example: Blood Shield 3, take 20 damage = 6 damage prevented, costs 600 LP, take 14 damage

### Server Configuration

**File:** `config/neovitae-server.toml`

Blood attribute multipliers under `[blood_attributes]`:

| Config Key | Default | Description |
|-----------|---------|-------------|
| `siphon_player_multiplier` | 100 | LP multiplier for Blood Siphon vs players |
| `siphon_mob_multiplier` | 10 | LP multiplier for Blood Siphon vs mobs |
| `shield_lp_cost_multiplier` | 100 | LP cost per damage point prevented by Blood Shield |

### Applying Attributes via Data Packs

Use standard NeoForge attribute modifier syntax on items or equipment:

```json
{
  "type": "minecraft:attribute_modifiers",
  "modifiers": [
    {
      "type": "neovitae:blood_siphon",
      "id": "mypack:blood_siphon_bonus",
      "amount": 2.0,
      "operation": "add_value",
      "slot": "mainhand"
    }
  ]
}
```

---

## Data-Driven Material System

Neo Vitae's ore processing system is fully data-driven. Materials (dusts, gravels, fragments) are defined in a JSON config file and items are generated at startup.

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
  "display_name": "Tin"
}
```

| Field | Required | Description |
|-------|----------|-------------|
| `name` | Yes | Unique material name. Used in item IDs (`neovitae:tin_dust`). |
| `color` | Yes | Hex color for item tinting (e.g., `#C8C8D0`). |
| `stages` | Yes | Processing stages to create: `fragment`, `gravel`, `dust`. |
| `smelt_to` | No | Item ID for smelting output. Omit to skip smelting recipes. |
| `smelt_xp` | No | XP from smelting (default: 0). |
| `ore_tag` | No | Tag for ore blocks (e.g., `c:ores/tin`). Enables ore processing recipes. |
| `raw_tag` | No | Tag for raw materials (e.g., `c:raw_materials/tin`). |
| `ingot_tag` | No | Tag for ingots/gems (e.g., `c:ingots/tin`). Enables ingot-to-dust recipe. |
| `display_name` | No | Override display name. Defaults to capitalized material name. |
| `id_overrides` | No | Map to override generated item IDs per stage. |

### Auto-Generated Content

For each material, the system automatically generates:

- **Items**: Fragment, gravel, and/or dust items with color-tinted base textures
- **Tags**: `c:fragments/{name}`, `c:gravels/{name}`, `c:dusts/{name}`
- **Smelting/blasting recipes**: Dust to output item (if `smelt_to` is defined)
- **Athanor recipes**: Full ore processing chain (ore/raw to fragments, fragments to gravel, gravel to dust, etc.)
- **Tabula Vitae recipes**: Ore to dust, fragments to gravel with corrupted dust
- **Item models and translations**: Generated in-memory, no files needed

All recipes use `neoforge:item_exists` conditions so they silently disable if the output item's mod is not installed.

### Auto-Discovery Command

**`/nvgenerate`** (or `/neovitae generate`) - Requires op permissions.

Scans all `c:ores/*` tags from installed mods and:
1. Discovers ore materials not already in the config
2. Looks up smelting recipes to find the output item
3. Falls back to `c:ingots/{name}` or `c:gems/{name}` tag matching
4. Extracts the ore's characteristic color from its block texture (filtering out stone-colored pixels)
5. Appends new material entries to the config

A game restart is required after running the command for new items to appear.

### First-Run Auto-Discovery

On the very first launch (when `materials.json` does not exist), the mod automatically runs the ore discovery process after the world loads. Any detected ores are added to the config and a notification is sent to players on join indicating a restart is needed.

### Adding a Custom Material Manually

1. Open `config/neovitae/materials.json`
2. Add a new entry to the JSON array
3. Restart the game
4. The new material's items, models, tags, and recipes appear automatically

### Removing a Material

Remove the entry from `materials.json` and restart. Existing items in the world will become unknown items.

---

## Tips & Best Practices

1. **Use `"replace": false`** in tags to add to existing lists instead of replacing them
2. **Test incrementally** - make one change at a time and verify it works
3. **Check the logs** - Neo Vitae logs warnings for invalid configurations
4. **Use JEI/REI** to verify recipe changes are applied
5. **Backup your world** before testing major balance changes

---

## Compatibility Notes

- All datamaps are synced to clients automatically
- Recipe changes require world reload (`/reload`)
- Tag changes require world reload
- Some changes may require game restart

---

## Getting Help

- GitHub Issues: <https://github.com/breakinblocks/NeoVitae/issues>
- Check existing datapack examples in `src/generated/resources/`
