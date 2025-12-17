# Neo Vitae - Modpack Maker's Guide

This guide covers all the data-driven systems in Neo Vitae that modpack makers can customize via datapacks. No Java code required!

## Table of Contents

1. [Getting Started](#getting-started)
2. [DataMaps (Primary Customization)](#datamaps-primary-customization)
   - [Blood Orb Stats](#blood-orb-stats)
   - [Sigil Stats](#sigil-stats)
   - [Ritual Stats](#ritual-stats)
   - [Imperfect Ritual Stats](#imperfect-ritual-stats)
   - [Tartaric Gem Capacities](#tartaric-gem-capacities)
3. [Sigil Types (Effect Definitions)](#sigil-types-effect-definitions)
4. [Recipe Types](#recipe-types)
5. [Tags](#tags)
6. [Loot Tables & Modifiers](#loot-tables--modifiers)
7. [Living Armor Upgrades](#living-armor-upgrades)
8. [Curios Integration](#curios-integration)
9. [KubeJS Event Hooks](#kubejs-event-hooks)
10. [Examples](#examples)

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
        │   ├── item/
        │   │   ├── blood_orb_stats.json
        │   │   ├── sigil_stats.json
        │   │   └── tartaric_gem_max.json
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

### Tartaric Gem Capacities

**Location:** `data/neovitae/data_maps/item/tartaric_gem_max.json`

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

### Blood Altar Recipes

**Location:** `data/neovitae/recipes/altar/`

```json
{
  "type": "neovitae:blood_altar",
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

### Tartaric Forge (Hellfire Forge) Recipes

**Location:** `data/neovitae/recipes/soul_forge/`

```json
{
  "type": "neovitae:soul_forge",
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

### Alchemy Table Recipes

**Location:** `data/neovitae/recipes/alchemy_table/`

```json
{
  "type": "neovitae:alchemy_table",
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

### Alchemical Reaction Chamber (ARC) Recipes

**Location:** `data/neovitae/recipes/arc/`

```json
{
  "type": "neovitae:arc",
  "input": { "item": "minecraft:iron_ore" },
  "tool": { "tag": "neovitae:arc_tool/explosives" },
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
| `arc_tool` | Tools usable in the ARC |
| `arc_tool/explosives` | Explosive tools (ore doubling) |
| `arc_tool/cutting_fluids` | Cutting tools |
| `arc_tool/furnace` | Smelting tools |
| `crystals/demon` | Demon crystal items |
| `charges` | Explosive charges |

### Entity Tags

**Location:** `data/neovitae/tags/entity_types/`

| Tag | Purpose |
|-----|---------|
| `telepose_blacklist` | Entities that cannot be teleposed |

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

### Blood Altar Craft Events

Hook into altar crafting to modify outputs or add side effects:

```javascript
// server_scripts/blood_magic_altar.js

// Modify or cancel altar crafting
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.BloodAltarCraftEvent$Crafting', event => {
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
NeoForgeEvents.onEvent('com.breakinblocks.neovitae.common.event.BloodAltarCraftEvent$Crafted', event => {
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
| `BloodAltarCraftEvent$Crafting` | Yes | Before altar craft completes |
| `BloodAltarCraftEvent$Crafted` | No | After altar craft completes |
| `ItemBindEvent` | Yes | When binding item to player |
| `SacrificialDaggerEvent` | Yes | When dagger drains health |
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
