# Pack Makers: DataMaps and Recipes

DataMaps and recipe JSON cover the bulk of practical NeoVitae customization. See [Pack Makers Overview](Pack-Makers-Overview) for the directory layout and command reference, and [Pack Makers Materials and Dungeons](Pack-Makers-Materials-and-Dungeons) for the material system, KubeJS hooks, and dungeon rooms.

## DataMaps (Primary Customization)

DataMaps are NeoForge's system for attaching data to registry entries. NeoVitae uses them extensively for balancing.

### Blood Orb Stats

**Location:** `data/neovitae/data_maps/item/blood_orb_stats.json`

Customize the tier, capacity, and fill rate of blood orbs. See [Blood Orbs and Anima](Blood-Orbs-and-Anima) for the player-facing system.

| Field | Type | Description |
|-------|------|-------------|
| `tier` | Integer | Orb tier (0-5). Determines recipe requirements. |
| `capacity` | Integer | Maximum EV the orb can store in the anima |
| `fillRate` | Integer | EV gained per tick when draining health |

**Example, Make Weak Orb hold more EV:**

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

**Default values:**

| Orb | Tier | Capacity | Fill Rate |
|-----|------|----------|-----------|
| Weak | 0 | 5,000 | 2 |
| Apprentice | 1 | 25,000 | 5 |
| Magician | 2 | 150,000 | 15 |
| Master | 3 | 1,000,000 | 25 |
| Archmage | 4 | 10,000,000 | 50 |

#### Blood Orb Internal Fluid Tank

Each Blood Orb has an internal fluid reservoir that stores Essentia Vitae. The capacity is computed as `4000 + (tier * 2000)` mB. When the altar fills the player's Anima, it also fills the orb's internal tank with the same amount.

**Dual-mode altar behavior:**

- **Orb has fluid (draining mode):** the altar drains the orb's internal tank at 10x the orb's normal fill rate. If the altar basin has at least 1,000 mB of room, the drained fluid goes into the altar. If the altar is nearly full (less than 1,000 mB of room), the drained fluid is channeled into the player's Anima instead.
- **Orb is empty (normal mode):** the altar operates normally, draining its own EV into the player's Anima at the orb's standard fill rate.

Players can pre-fill orbs with EV (for example, from fluid pipes or the [Athanor](Athanor-and-Materials)) and then use those orbs to rapidly refill an altar or top off their network. Debug with `/neovitae setorbfill <amount>`.

---

### Sigil Stats

**Location:** `data/neovitae/data_maps/item/sigil_stats.json`

Customize EV costs and effect parameters for all sigils. See [Sigils](Sigils) for the player-facing system.

| Field | Type | Description |
|-------|------|-------------|
| `lp_cost` | Integer | EV cost per activation |
| `drain_interval` | Integer | Ticks between drains for toggleable sigils (default: 100 = 5s) |
| `range` | Integer | Horizontal radius for area effects (optional) |
| `vertical_range` | Integer | Vertical range for area effects (optional) |
| `effect_duration` | Integer | Duration in ticks for potion effects (optional) |
| `effect_level` | Integer | Potion effect amplifier level (optional) |

**Example, Reduce Air Sigil cost and make Fast Miner cheaper:**

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

**Default sigil costs:**

| Sigil | EV Cost | Notes |
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

Customize activation costs, refresh costs, and range limits for rituals. See [Rituals](Rituals) for the catalog.

| Field | Type | Description |
|-------|------|-------------|
| `activation_cost` | Integer | EV cost to activate the ritual |
| `refresh_cost` | Integer | EV cost per refresh tick |
| `refresh_time` | Integer | Ticks between refreshes (default: 20 = 1s) |
| `crystal_level` | Integer | Required activation crystal (0=weak, 1=awakened, 2=creative) |
| `range_limits` | Object | Map of range names to limit objects |

> **Ritual of the Mason:** its `placerRange` limits are normally derived from the Raw Spiritus in the chunk (5,000 blocks at reach 15, rising to 20,000 at reach 24 and 80,000 at reach 40). Supplying an explicit `range_limits.placerRange` entry replaces that with a fixed cap and disables the Spiritus scaling entirely. Note the all-or-nothing behaviour above applies: setting only `max_volume` also accepts the codec defaults of 256 for both radii.

| `enabled` | Boolean | Whether the ritual is enabled (default: true). Disabled rituals cannot be activated and are hidden from JEI. |
| `per_operation` | Boolean | Whether `refresh_cost` is charged once per completed operation rather than continuously (default: false). Affects how the cost is worded in the guide book; it does not change what the ritual charges. Set it on rituals that only draw EV when they actually process something, such as Sentient Extraction. |
| `consume_books` | Boolean | Ritual of Enchanted Vitae only. When true the rite consumes one of each enchanted book that actually contributed an enchantment to the item (default: false, books are returned unspent). Books that offered nothing the item could take are never consumed. The in-game guide notes that packs may change this. |

**Range limit object:**

```json
{
  "maxVolume": 1000,
  "maxHorizontalRadius": 10,
  "maxVerticalRadius": 10
}
```

**Example, Make Water Ritual cheaper and faster:**

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

**Example, Customize Well of Suffering range:**

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

Customize imperfect rituals, simple one-time effects triggered by placing a block on an imperfect ritual stone.

| Field | Type | Description |
|-------|------|-------------|
| `activation_cost` | Integer | EV cost for activation |
| `block` | String | Block registry name (e.g. `minecraft:water`) |
| `block_tag` | String | Alternative: use a block tag instead |
| `consume_block` | Boolean | Whether the catalyst block is consumed (default: false) |
| `lightning_effect` | Boolean | Whether lightning strikes on activation (default: true) |
| `enabled` | Boolean | Whether the ritual is enabled (default: true) |

**Example, custom imperfect ritual:**

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

**Default imperfect rituals:**

| Ritual | Block | Cost | Consumes | Lightning |
|--------|-------|------|----------|-----------|
| Rain | water | 5,000 | No | No |
| Zombie Resurrection | coal_block | 5,000 | No | Yes |
| Resistance | bedrock | 5,000 | No | No |

---

### Spiritus Gem Capacities

**Location:** `data/neovitae/data_maps/item/spiritus_gem_max.json`

Customize how much [Spiritus](Spiritus-Aspects-and-Crystals) each soul gem tier can hold.

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

**Default capacities:**

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

```json
{
  "effect": {
    "type": "neovitae:effect_type"
  },
  "lp_cost_air": 50,
  "lp_cost_block": 50,
  "toggleable": false,
  "drain_interval": 100
}
```

**Built-in effect types:**

- `neovitae:air`, launch player into air
- `neovitae:water`, place water source
- `neovitae:lava`, place lava source
- `neovitae:void`, remove fluids
- `neovitae:blood_light`, create light source
- `neovitae:divination`, show altar/network info
- `neovitae:teleposition`, teleport to bound location
- `neovitae:fast_miner`, haste effect
- `neovitae:green_grove`, accelerate growth
- `neovitae:magnetism`, pull items
- `neovitae:frost`, freeze water
- `neovitae:suppression`, push away fluids
- `neovitae:phantom_bridge`, create phantom blocks

---

## Altar Tier Customization

**Location:** `data/neovitae/neovitae/altar_tier/`

Each altar tier (Weak through Transcendent) is a JSON entry in the `neovitae:altar_tier` datapack registry. Pack authors can re-shape the multiblock geometry **and** the visual effects each tier emits without touching code; the validator, the Modonomicon multiblock preview, and the in-world particle / render code all read from the same files. See [Ara Vitae and Runes](Ara-Vitae-and-Runes) for the player-facing tier ladder.

A bundled example datapack at `examples/datapacks/neovitae_classic_altar/` ships a complete square-layout altar definition and is the easiest starting point.

### File Structure

```json
{
  "tier": 2,
  "components": [
    { "pos": [0, 0, 0], "valid": "neovitae:ara_vitae", "upgrade": false },
    { "pos": [4, 1, 0], "valid": "#neovitae:altar/t3_capstones", "upgrade": false }
  ],
  "effects": [
    {
      "type": "cap_orbit_life_pulse",
      "color": 12268288,
      "origins": [[4, 1, 0], [-4, 1, 0], [0, 1, 4], [0, 1, -4]]
    }
  ]
}
```

| Field | Type | Description |
|-------|------|-------------|
| `tier` | Integer | 0-5. Resolved index into the runtime tier list. |
| `components` | Array | Block requirements for the multiblock. |
| `effects` | Array | Optional. Visual effects emitted while this tier is active. |

### `components`

| Field | Type | Description |
|-------|------|-------------|
| `pos` | `[x, y, z]` | Integer offset from the Ara Vitae core. |
| `valid` | String | Block id (`neovitae:ara_vitae`) or tag reference (`#neovitae:altar/runes`). |
| `upgrade` | Boolean | `true` if the slot accepts a player-installed rune (cycles through rune blocks in the preview); `false` for purely structural blocks. |
| `optional` | Boolean | Optional. When `true`, the position validates as **either air or the configured matcher**. The bundled tiers use this to make the pillar columns underneath each cap optional. Defaults to `false`. |

Tag references resolve against live block tags, so a pack can keep the same skeleton and just retag which blocks count as runes, pillars, or capstones via the existing `altar/*` tags.

### `effects`

Effects are **cumulative**; a tier 5 altar plays its own effects plus every lower tier's, so each tier definition only needs to add new visuals (or replace inherited ones by overriding the lower tier's JSON entirely).

| Field | Type | Description |
|-------|------|-------------|
| `type` | Enum | Visual style (see below). |
| `origins` | Array of `[x, y, z]` | Offsets from the Ara Vitae core where the effect anchors. |
| `color` | Integer | Tint color packed as decimal RGB (e.g. `0x8800CC` = `8913100`). Optional, defaults to white. |

**Effect types:**

| Type | What it does |
|------|--------------|
| `cap_orbit_life_pulse` | All origins synchronously orbit a particle ring, then fire a life-pulse stream into the altar core. Best for cap rings on the lowest active tier. |
| `cap_orbit_spiral_staggered` | Each origin orbits independently with staggered phase offsets, ending in a spiralling stream. Slower, more chaotic. |
| `cap_burst` | Low-rate ambient particle bursts at each origin (1 particle per ~5 ticks). Good for "the cap is awake" ambiance. |
| `cap_crystal_cascade` | Downward cascading particle column above each origin. Visually expensive on large rings. |
| `cap_render_hover_array` | **Client-side only.** Hovers a rotating alchemy-array texture above each origin and emits matching cascade particles. Use to call out the highest-prestige caps. |

### Default Tiers

The bundled circular layout fires these effects (cumulative):

| Tier | Cap distance | Effect added |
|------|--------------|--------------|
| Mage (2) | 4 (cardinals) | `cap_orbit_life_pulse` |
| Master (3) | 6 (cardinals) | `cap_orbit_spiral_staggered` |
| Archmage (4) | 9 (cardinals) | `cap_burst` + `cap_render_hover_array` |
| Transcendent (5) | 12 (cardinals) | `cap_crystal_cascade` |

### Authoring Tips

- **Origins do not have to coincide with `components` positions.** The effect engine reads offsets; anchor visuals on empty air inside the multiblock if that reads better.
- **Omit `effects` entirely** for a quiet tier (this is what Weak and Apprentice do by default).
- **Tag changes propagate automatically.** Retag what counts as a pillar or capstone via the existing `altar/*` tags and validation/preview update without altering the tier JSON.
- **Multiblock preview stays in sync.** NeoVitae builds the in-book Scriptura Vitae diagram from the loaded altar tier data at server start. To override the preview cosmetically without changing validation, drop a Modonomicon multiblock JSON at `data/neovitae/modonomicon/multiblocks/altar_<one|two|three|four|five|six>.json` and the runtime will leave it untouched.

---

## Recipe Types

NeoVitae adds several recipe types that can be customized via datapacks.

### Ingredient Syntax Across Minecraft Versions

Minecraft 1.21.2 changed how vanilla spells ingredients: `{ "item": "minecraft:sand" }`
became `"minecraft:sand"`, and `{ "tag": "c:sands" }` became `"#c:sands"`.

Every NeoVitae recipe type accepts **both** spellings on **both** the 1.21.1 and the
26.1 build, so a single set of recipe files works on either version with no edits:

| Meaning | 1.21.1 style | 26.1 style |
|---------|--------------|------------|
| One item | `{ "item": "minecraft:sand" }` | `"minecraft:sand"` |
| An item tag | `{ "tag": "c:sands" }` | `"#c:sands"` |
| Several options | `[{ "item": "a" }, { "tag": "b" }]` | `["a", "#b"]` |
| One fluid | `{ "fluid": "minecraft:water" }` | `"minecraft:water"` |
| A fluid tag | `{ "tag": "c:water" }` | `"#c:water"` |

Pick one style per field; the two cannot be mixed inside a single list. Item results
(`{ "id": "...", "count": 1 }`) and fluid amounts (`{ "id": "...", "amount": 50 }`)
already use the same shape on both versions.

This covers NeoVitae's own recipe types only. Vanilla recipe types such as
`minecraft:crafting_shaped` follow vanilla's rules, so those still need
version-specific files.

NeoVitae's own generated recipes are written in whichever style is native to the
build, so the files shipped inside the jar will not always match the style you author.

### Ara Vitae Recipes

**Location:** `data/neovitae/recipe/altar/`

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
| `totalBlood` | Total EV required for crafting |
| `craftSpeed` | EV consumed per craft tick |
| `drainSpeed` | Max EV drained from altar per tick |

### Hellfire Forge Recipes

**Location:** `data/neovitae/recipe/hellfire_forge/`

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

**Location:** `data/neovitae/recipe/tabula_vitae/`

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

**Location:** `data/neovitae/recipe/athanor/`

```json
{
  "type": "neovitae:athanor",
  "tool": { "tag": "neovitae:athanor_tool/explosives" },
  "inputs": [
    { "item": "minecraft:iron_ore" }
  ],
  "guaranteed_outputs": [
    {
      "id": "neovitae:iron_fragment",
      "count": 3
    }
  ],
  "chance_outputs": [
    {
      "item": { "id": "neovitae:iron_fragment" },
      "chance": 0.5
    }
  ],
  "input_fluid": {
    "ingredient": { "fluid": "minecraft:water" },
    "amount": 100
  },
  "output_fluid": {
    "id": "minecraft:lava",
    "amount": 50
  }
}
```

| Field | Required | Description |
|-------|----------|-------------|
| `tool` | yes | The Athanor tool the recipe runs on, normally an `athanor_tool/*` tag |
| `inputs` | yes | Up to six ingredients, matched against the input slots in any order |
| `guaranteed_outputs` | no | Item stacks produced on every craft. Defaults to none |
| `chance_outputs` | no | Item stacks produced per the given `chance`. Defaults to none |
| `input_fluid` | no | Fluid consumed per craft, as `{ "ingredient": ..., "amount": N }` |
| `output_fluid` | no | Fluid produced per craft |
| `spiritus_costs` | no | Map of spiritus type to amount drained from the chunk per craft |
| `spiritus_boost` | no | When `true`, local raw spiritus can yield a bonus copy of the first guaranteed output |

Both output lists are optional and default to empty, so a recipe that only produces a
fluid may omit them entirely. Omit a list rather than writing an entry for `minecraft:air`;
air is not a valid item stack and will fail to load.

A `chance` above `1.0` always produces at least one copy and rolls the remainder for a
second, so `2.5` yields two copies plus a 50% chance of a third.

### Alchemy Array Recipes

**Location:** `data/neovitae/recipe/array/`

```json
{
  "type": "neovitae:alchemy_array",
  "base_input": { "item": "neovitae:arcane_ash" },
  "added_input": { "item": "minecraft:feather" },
  "result": { "id": "neovitae:air_sigil" }
}
```

### Meteor Recipes

**Location:** `data/neovitae/recipe/meteor/`

Defines what blocks spawn when the Meteor ritual is activated.

### Flask Recipes

**Location:** `data/neovitae/recipe/flask/`

Flask recipes use a base class with subtypes (`FlaskEffectRecipe`, `FlaskLengthRecipe`, etc.) and operate on `ItemAlchemyFlask`, a reusable potion container with data components. JEI uses a flask subtype interpreter to display per-variant recipe lookups; the creative tab includes all flask variants. See [Tabula Vitae, Flasks and Anointments](Tabula-Vitae-Flasks-and-Anointments) for the player-facing system.

### Sentient Downgrade Recipes

**Location:** `data/neovitae/recipe/downgrade/`

Type `neovitae:sentient_downgrade`. Each recipe maps a catalyst `input` ingredient to a `sentient_upgrade` id; the Ritual of Sentient Penance consumes one catalyst thrown onto the Master Ritual Stone and inscribes one level of that downgrade onto the standing player's worn Sentient chestplate. See the [Sentient Armor](Sentient-Armor) page for the player-facing system and the Sentient Armor Upgrades section below for the upgrade data structure.

```json
{
  "type": "neovitae:sentient_downgrade",
  "input": { "item": "minecraft:glass_bottle" },
  "sentient_upgrade": "neovitae:quenched"
}
```

---

## Tags

Tags control which blocks, items, entities, fluids and damage types each NeoVitae system recognizes. Every tag the mod reads is listed below. Add to a tag by creating the matching JSON in your datapack with `"replace": false`; tags marked *empty by default* ship no JSON at all, so the file you create is the whole list.

All paths are relative to `data/neovitae/tags/` unless a different namespace is given.

### Block Tags

**Location:** `data/neovitae/tags/block/`

| Tag | Default contents | Purpose |
|-----|------------------|---------|
| `altar/runes` | All tier 1 and tier 2 rune blocks | Blocks that count as altar runes. Referenced as `#neovitae:altar/runes` by every shipped altar tier definition. |
| `altar/pillars` | Empty (any solid block) | Blocks accepted in an altar tier's pillar positions. Empty means any solid block; populate it to restrict pillar materials. |
| `altar/t3_capstones` | Blood Stained Glass | Capstone block for the tier 3 altar. |
| `altar/t4_capstones` | Bloodstone Brick | Capstone block for the tier 4 altar. |
| `altar/t5_capstones` | Hellforged Block | Capstone block for the tier 5 altar. |
| `altar/t6_capstones` | Spiritus crystal clusters | Capstone block for the tier 6 altar. |
| `altar/bloodstones` | Bloodstone blocks | Bloodstone family. Available for altar tier definitions that want to reference bloodstone by tag. |
| `altar/pulse_on_crafting` | Redstone Lamp, Note Block | When one of these blocks sits directly under an Ara Vitae, the altar emits a redstone pulse each time it finishes a craft. |
| `altar/anima_comparator` | Blood Stained Glass | When one of these blocks sits directly under an Ara Vitae, a comparator reads the Anima EV of the bound orb on the altar instead of the altar's own tank. |
| `incense_path/level_0` to `level_10` | Level 0: Dirt Path, `#minecraft:stone_bricks`. Each higher level includes the level below it. | Valid incense path blocks by ring distance from the Incense Altar. A block tagged at level N is accepted at ring N and every ring closer to the altar. |
| `tranquility/plant` | Empty | Plant-type tranquility for the Incense Altar. |
| `tranquility/crop` | Empty | Crop-type tranquility. |
| `tranquility/tree` | Empty | Tree-type tranquility. |
| `tranquility/earthen` | Sands of Vitae | Earthen-type tranquility. |
| `tranquility/water` | Empty | Water-type tranquility. |
| `tranquility/fire` | Empty | Fire-type tranquility. |
| `tranquility/lava` | Empty | Lava-type tranquility. |
| `geode_harvestable` | Amethyst Cluster, `#c:clusters` | Mature crystal clusters the Crystallum Fractura ritual harvests alongside Spiritus Crystals. Add other mods' clusters here. |
| `geode_acceleratable` | Budding Amethyst | Budding blocks whose growth the Crystallum Fractura ritual accelerates. |
| `mushroom_stem` | Crimson Stem, Warped Stem and their stripped variants | Stem blocks the Fungal Charge treats as part of a giant mushroom. |
| `mushroom_hyphae` | Crimson Hyphae, Warped Hyphae and their stripped variants | Hyphae blocks the Fungal Charge treats as part of a giant mushroom. |
| `mundane_block` | Empty | Blocks whose drops the Voiding anointment deletes. The Voiding anointment does nothing until a pack fills this tag. |
| `telepose_blacklist` | Bedrock, barriers, command blocks, structure blocks, jigsaw blocks and portal blocks | Blocks the Teleposer refuses to move. See [Pack Makers Materials and Dungeons](Pack-Makers-Materials-and-Dungeons). |
| `generative_ores` | Filled at runtime | Ore blocks of every generative material. Populated automatically from `config/neovitae/materials.json`; see [Pack Makers Materials and Dungeons](Pack-Makers-Materials-and-Dungeons). |

**Tranquility tags and the tranquility datamap.** The seven `tranquility/*` tags are read through the `neovitae:tranquility` block datamap at `data/neovitae/data_maps/block/tranquility.json`. The shipped datamap maps each tag to its type, so adding a block to `tranquility/plant` gives it a plant contribution of `1.0`. For a different strength, or to override a vanilla block, write a datamap entry directly:

```json
{
  "values": {
    "some_mod:glowing_moss": { "type": "plant", "value": 0.5 },
    "#some_mod:hot_stones": { "type": "fire" }
  }
}
```

`type` is one of `plant`, `crop`, `tree`, `earthen`, `water`, `fire`, `lava`. `value` defaults to `1.0`. Water, lava and vanilla crop blocks are also detected at runtime without any datamap entry.

### Item Tags

**Location:** `data/neovitae/tags/item/`

| Tag | Default contents | Purpose |
|-----|------------------|---------|
| `spiritus_gems` | Petty through Grand Spiritus Gems | Items the Hellfire Forge treats as a Spiritus Gem (gem slot detection and gem-output recipes). |
| `spiritus_capable` | Empty | Items that can receive spiritus storage through Hellfire Forge infusion. |
| `crystals/demon` | All five Spiritus Crystal items | Spiritus crystal items. Used as a recipe ingredient. |
| `charges` | Every explosive charge (shaped, deforester, veinmine, fungal and their upgrades) | Explosive charge items. Anointments are not consumed when a charge is used, and the Smelting and Voiding loot modifiers skip charge drops. |
| `blood_mending_blacklist` | Empty | Items the Blood Mending upgrade will not repair. |
| `sentient_repair` | Binding Reagent | Repair material for Sentient tools, Sentient armor and the Lex Vitae in an anvil. |
| `vitae_stone` | Deepslate | Stone-type ingredient used by a large number of early recipes (component frames, Tabula Rasa, and others). Retag this to change the base stone of the mod. |
| `sentient_upgrade_set` | `#neovitae:sentient_upgrade_set/sentient` | Any armor piece that participates in the Sentient Armor upgrade system. Equipping or removing one recalculates the full-set bonuses and Curios slots. |
| `sentient_upgrade_set/sentient` | Sentient Helmet, Chestplate, Leggings, Boots | The four pieces that make up the Sentient Armor set. Rituals that act on the set (Evolve, Penance, Upgrade Removal) require the chest piece to be in this tag. |
| `anointments` | Every anointment item | Anointment items accepted by the smithing table apply recipe. *26.1 only.* |
| `anointable` | Empty | Base items accepted by the smithing table apply recipe. Fill it with the tools that should accept anointments through the smithing table. *26.1 only.* |
| `anointable/melee` | `#minecraft:swords`, `#minecraft:axes` | Items that can receive melee-category anointments. |
| `anointable/mining` | `#minecraft:pickaxes`, `#minecraft:shovels`, `#minecraft:axes` | Items that can receive mining-category anointments. |
| `anointable/bows` | Bow, Crossbow | Items that can receive bow-category anointments. |
| `anointable/weapons` | `#neovitae:anointable/melee`, `#neovitae:anointable/bows` | Items that can receive any-weapon anointments. |
| `altar/runes`, `altar/bloodstones`, `altar/t3_capstones` to `t6_capstones`, `altar/anima_comparator` | Item forms of the matching block tags | Item-side mirrors of the altar block tags, for recipes and item matching. Keep them in step with the block tags when you retag altar materials. |

### Item Category Tags

Nothing in the mod reads these. They exist so filters in other mods (backpack tag filters, pipe filters, sorting systems) and datapack recipes can match a whole family of NeoVitae items at once.

**Location:** `data/neovitae/tags/item/`

| Tag | Default contents | Purpose |
|-----|------------------|---------|
| `orbs` | All six tier tags below | Every Orb of Vitae. |
| `orbs/tier_1` to `orbs/tier_6` | One orb each: Novicius, Discipulus, Magus, Veneficus, Dominus, Divinus | A single orb tier. |
| `tools` | The three tool tags below plus the Spiritus Gauge | Every hand-held utility item that is not a weapon or mining tool. Also added to `c:tools`. |
| `tools/ritual` | Ritual Diviner (both), Ritual Configurator, Ritual Designer | Items used to lay out, configure and inspect rituals. |
| `tools/scribe` | Arcane Scribe Tool, Air, Fire, Water, Earth and Tenebrae Scribe Tools | Alchemy array and rune inscription tools. |
| `tools/routing` | Node Router | Routing network tools. The Node Router is also in `c:tools/wrench`. |
| `sigils` | Every sigil, including the Sigil of the Damned | All sigil items. |
| `activation_crystals` | Weak, Awakened and Creative Activation Crystals | Ritual activation crystals. |
| `teleposer_foci` | Teleposer Focus, Enhanced Teleposer Focus, Reinforced Teleposer Focus | Teleposer foci. |
| `dungeon_keys` | Simple, Mine, Mine Entrance, Standard and Boss Keys | Dungeon keys. |
| `throwing_daggers` | Throwing Dagger, Amethyst Throwing Dagger, Syringe Throwing Dagger, Tipped Throwing Dagger | Throwing daggers. |

### Athanor Tool Tags

**Location:** `data/neovitae/tags/item/athanor_tool/`

Every Athanor recipe names its tool by tag. The parent tag `athanor_tool` contains every child, and `athanor_tool/furnace` contains the three furnace children.

| Tag | Default contents | Purpose |
|-----|------------------|---------|
| `athanor_tool` | All tags below | Anything the Athanor accepts in its tool slot. |
| `athanor_tool/explosives` | Explosive Powder, Primitive Explosive Cell, Hellforged Explosive Cell | Ore crushing and doubling. |
| `athanor_tool/cutting_fluids` | Basic, Intermediate, Advanced Cutting Fluid | Cutting recipes. |
| `athanor_tool/resonator` | Resonator, Primitive Crystalline Resonator, Hellforged Resonator | Resonance recipes (dungeon stone conversion and others). |
| `athanor_tool/reverter` | Sanguine Reverter | Reversion recipes that break runes and other crafted blocks back into parts. |
| `athanor_tool/hydration` | Primitive Hydration Cell | Hydration recipes (clay from dust or terracotta, and others). |
| `athanor_tool/lingering_flask` | Lingering Alchemy Flask | Athanor potion recipes such as tipped throwing daggers. |
| `athanor_tool/furnace` | The three furnace tags | Any furnace-type tool. |
| `athanor_tool/furnace/smelting` | Primitive Furnace Cell, Lava Crystal | Runs vanilla smelting recipes in the Athanor. |
| `athanor_tool/furnace/blasting` | Empty | Runs vanilla blasting recipes in the Athanor. |
| `athanor_tool/furnace/smoking` | Empty | Runs vanilla smoking recipes in the Athanor. |

### Anointable Item Tags

Each anointment is restricted to a specific item tag. If a tool's id is not in the matching tag, right-clicking the anointment onto it (or applying it in a smithing table) does nothing. Add modded weapons, tools, or custom bows to these tags to make them valid targets.

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
| Vampiric Edge (Spiritus Drain) | `neovitae:anointable/weapons` |
| Repairing Salve (Weapon Repair) | `neovitae:anointable/weapons` |

**Example, adding a modded bow to Iron Tip:**

```json
{
  "replace": false,
  "values": [
    "tetra:modular_bow",
    "some_mod:magic_bow"
  ]
}
```

For one-off anointment applicability (for example restricting Honing Oil to Netherite swords only), a Java addon can declare its own tag such as `neovitae:anointable/iron_tip_only` and pass it to `appliesTo(...)` in `AnointmentRegistrar`. The call accepts any `TagKey<Item>`.

### Entity Tags

**Location:** `data/neovitae/tags/entity_type/`

| Tag | Default contents | Purpose |
|-----|------------------|---------|
| `telepose_blacklist` | Empty | Entities the Teleposer refuses to move. |
| `well_of_suffering_blacklist` | Empty | Entities the Well of Suffering ritual will not damage for EV. |
| `ritual_boss_blacklist` | Ender Dragon, Wither, Warden | Entities treated as bosses by rituals. The Grounding ritual skips them unless it is running with Spiritus Invictus. |
| `no_sacrifice` | Empty | Entities whose sacrifice value is zero. The Well of Suffering, Torment Nexus and dungeon spike traps grant no EV for them. Use it for summoned servants or utility mobs. |
| `no_sentient_training` | `dummmmmmy:target_dummy` (optional) | Entities that do not train Sentient Armor upgrades when hit. Add target dummies and other punching bags here. |
| `loyal_friends_blacklist` | Empty | Tamed animals the Array of Loyal Friends will not summon, and will not store or revive on death. |
| `deny_imprisonment` | Ender Dragon, Wither, Warden | Mobs the Array of Imprisonment ignores. A spawner next to the array will not be rebound to one of these when it dies nearby. |

### Fluid Tags

**Location:** `data/neovitae/tags/fluid/`

| Tag | Default contents | Purpose |
|-----|------------------|---------|
| `essentia_vitae` | Essentia Vitae source and flowing | Any Essentia Vitae fluid. |
| `essentia_vitae_source` | Essentia Vitae source | Still Essentia Vitae only. |
| `essentia_vitae_flowing` | Essentia Vitae flowing | Flowing Essentia Vitae only. |
| `animated_spiritus` | Animated Spiritus source and flowing | Any Animated Spiritus fluid. |
| `animated_spiritus_source` | Animated Spiritus source | Still Animated Spiritus only. |
| `animated_spiritus_flowing` | Animated Spiritus flowing | Flowing Animated Spiritus only. |

The Array of Liquified Experience reads the conventional `c:experience` fluid tag, which NeoVitae populates with its own Liquified Experience. To make the array use another mod's experience fluid, set `liquified_experience.preferred_fluid` in the server config rather than editing the tag.

### Damage Type Tags

**Location:** `data/neovitae/tags/damage_type/`

| Tag | Default contents | Purpose |
|-----|------------------|---------|
| `self_sacrifice` | `neovitae:self_sacrifice` | Damage that counts as self-sacrifice. The Self Sacrifice Sentient Armor upgrade only trains from damage in this tag. |
| `tough_ignored` | `#minecraft:is_fire`, `#minecraft:is_explosion`, `#minecraft:is_fall`, `#minecraft:is_projectile` | Damage types that do not train the Tough (physical protection) Sentient Armor upgrade. |

### Altar Tier Tags

**Location:** `data/neovitae/tags/neovitae/altar_tier/`

| Tag | Default contents | Purpose |
|-----|------------------|---------|
| `valid_tiers` | `weak`, `apprentice`, `mage`, `master`, `archmage`, `transcendent` | Altar tier definitions the multiblock validator and the guide book consider. A custom tier must be added here or it is never checked. See [Altar Tier Customization](#altar-tier-customization). |

### Sentient Upgrade Tags

**Location:** `data/neovitae/tags/neovitae/sentient_upgrades/`

These are covered under [Upgrade Tags](#upgrade-tags) in the Sentient Armor Upgrades section below.

### Curios Slot Tags

**Location:** `data/curios/tags/item/`

When Curios is installed, these tags decide which NeoVitae items fit each slot. Add your own items to them, or remove NeoVitae's with `"replace": true`.

| Tag | Default contents |
|-----|------------------|
| `charm` | Fast Miner, Green Grove, Magnetism, Frost, Holding, Divination, Seer and Suppression sigils, Experience Tome, Spiritus Gauge |
| `necklace` | Petty through Grand Spiritus Gems |
| `bracelet` | Upgrade Tome, Training Bracelet |
| `sentient_armour_socket` | Everything in the three tags above |

### Conventional Tags NeoVitae Populates

NeoVitae adds its own items and blocks to the shared `c:` tags so other mods' recipes pick them up. These are safe to extend but rarely need editing.

| Tag | Contents |
|-----|----------|
| `c:ingots/hellforged`, `c:ingots` | Hellforged Ingot |
| `c:raw_materials/hellforged`, `c:raw_materials` | Raw Demonite |
| `c:storage_blocks/hellforged` (block and item) | Hellforged Block |
| `c:ores` (block and item) | Demonite Ore |
| `c:dusts/sulfur`, `c:dusts/saltpeter`, `c:dusts/corrupted`, `c:tiny_dusts/corrupted` | The matching NeoVitae dusts |
| `c:fragments/<name>`, `c:gravels/<name>`, `c:dusts/<name>` | Generated per material by the material system; see [Pack Makers Materials and Dungeons](Pack-Makers-Materials-and-Dungeons) |
| `c:tools` | `#neovitae:tools` |
| `c:tools/wrench` | Node Router |
| `c:experience` (fluid) | Liquified Experience |

---

## Loot Tables and Modifiers

### Global Loot Modifiers

**Location:** `data/neovitae/loot_modifiers/`

NeoVitae includes loot modifiers for anointments:

| Modifier | Effect |
|----------|--------|
| `smelting.json` | Auto-smelts drops (Smelting anointment) |
| `voiding.json` | Voids mundane blocks (Voiding anointment) |

**Note:** Silk Touch, Fortune, and Looting anointments are handled via NeoForge's `GetEnchantmentLevelEvent`, making them behave like real enchantments. This ensures proper compatibility with shulker boxes and other containers, and allows Fortune/Looting to stack with existing enchantments.

### Dungeon Loot Tables

**Location:** `data/neovitae/loot_table/chests/mines/`

Customize dungeon chest contents:

- `decent_loot.json`, general good items
- `food_loot.json`, food supplies
- `mine_key_loot.json`, keys and special items
- `ore_loot.json`, ore materials
- `smithy_loot.json`, crafting materials

---

## Sentient Armor Upgrades

Sentient armor upgrades are defined via datapack registries. See [Sentient Armor](Sentient-Armor) for the player-facing system. Each upgrade has levels with XP requirements and effects.

**Location:** `data/neovitae/living_upgrade/`

Upgrades use effect components:

- Attribute modifiers (speed, damage, health, etc.)
- Status effects (fire resistance, etc.)
- Special behaviors (repair, elytra flight, etc.)

### Upgrade Tags

Control upgrade behavior with tags in `data/neovitae/tags/neovitae/sentient_upgrades/`:

| Tag | Purpose |
|-----|---------|
| `trainer` | Upgrades that gain XP through activity |
| `sentient_start` | Upgrades present on freshly bound armor |
| `is_downgrade` | Negative upgrades (red tooltip; applied via the Sentient Penance ritual) |
| `is_scrappable` | Can be removed individually |
| `tooltip_order` | Display order of upgrades in the armor tooltip |
| `tooltip_hide` | Hidden from tooltips |
| `sentient_blacklist` | Upgrades that cannot be applied to Sentient Armor |

**Example, blacklist an upgrade from Sentient Armor:**

Useful for preventing overpowered custom upgrades or upgrades that conflict with other mods. Create `data/neovitae/tags/neovitae/sentient_upgrades/sentient_blacklist.json`:

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

`entities/bmplayerslots.json` defines curios slots for players.

### Sentient Armor Socket

`slots/living_armour_socket.json` configures socket slots for sentient armor upgrades.

---

## Custom Player Attributes

NeoVitae registers several custom player attributes that can be modified via equipment, effects, data packs, or addon mods using standard Minecraft attribute modifiers.

### Attribute Reference

| Attribute | Registry ID | Default | Max | Description |
|-----------|------------|---------|-----|-------------|
| Self Sacrifice Multiplier | `neovitae:player.self_sacrifice_multiplier` | 1.0 | 100.0 | Multiplier for EV gained from self-sacrifice (PercentageAttribute) |
| Bonus Sacrifice | `neovitae:bonus_sacrifice` | 0.0 | 1000.0 | % bonus to EV gained from Lamina Exhauriens mob kills |
| Bonus Self Sacrifice | `neovitae:bonus_self_sacrifice` | 0.0 | 1000.0 | % bonus to EV gained from Lamina Maleficus self-sacrifice |
| Bonus Spiritus | `neovitae:bonus_spiritus` | 0.0 | 1000.0 | % bonus to Spiritus drops from sentient weapons and soul snares |
| Sigil Cost Reduction | `neovitae:sigil_cost_reduction` | 0.0 | 100.0 | % reduction to all sigil EV costs (capped near zero, minimum 1 EV) |
| Blood Siphon | `neovitae:blood_siphon` | 0.0 | 1024.0 | Converts damage dealt into EV. Base EV = min(attribute, damage), then multiplied |
| Blood Shield | `neovitae:blood_shield` | 0.0 | 10.0 | Reduces incoming damage by 10% per point (capped at 99%), drains EV for prevented damage |

### Blood Siphon Details

When a player with Blood Siphon deals damage:

- **EV gained** = min(blood_siphon_value, damage_dealt) × multiplier
- **vs Players (PvP):** EV is drained directly from the target player's anima and added to the attacker's. True EV transfer, the victim loses what the attacker gains. Multiplier = configurable (default: 100).
- **vs Mobs (PvE):** EV is generated from nothing and added to the attacker's network. Multiplier = configurable (default: 10).
- Example (PvE): Blood Siphon 5, deal 10 damage to a mob = 5 × 10 = 50 EV gained.
- Example (PvP): Blood Siphon 5, deal 10 damage to a player = 5 × 100 = 500 EV stolen from their network.

### Blood Shield Details

When a player with Blood Shield takes damage:

- **Damage reduction** = 10% per attribute point (Blood Shield 5 = 50% reduction).
- **Hard cap:** 99% maximum reduction (at Blood Shield 10).
- **EV cost** = damage_prevented × configurable multiplier (default: 100).
- If insufficient EV: partial shield uses available EV, remaining damage passes through.
- Example: Blood Shield 3, take 20 damage = 6 damage prevented, costs 600 EV, take 14 damage.

### Server Configuration

**File:** `config/neovitae-server.toml`

Blood attribute multipliers under `[blood_attributes]`:

| Config Key | Default | Description |
|-----------|---------|-------------|
| `siphon_player_multiplier` | 100 | EV multiplier for Blood Siphon vs players |
| `siphon_mob_multiplier` | 10 | EV multiplier for Blood Siphon vs mobs |
| `shield_lp_cost_multiplier` | 100 | EV cost per damage point prevented by Blood Shield |

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

## Worked Examples

### Easier Early Game

`data/neovitae/data_maps/item/blood_orb_stats.json`:

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

`data/neovitae/data_maps/item/sigil_stats.json`:

```json
{
  "values": {
    "neovitae:air_sigil": { "lp_cost": 25 },
    "neovitae:water_sigil": { "lp_cost": 50 },
    "neovitae:divination_sigil": { "lp_cost": 0 }
  }
}
```

### Harder Rituals

`data/neovitae/data_maps/neovitae/ritual/ritual_stats.json`:

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

### Disabling Specific Rituals

Disabled rituals cannot be activated and are automatically hidden from JEI.

`data/neovitae/data_maps/neovitae/ritual/ritual_stats.json`:

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

For imperfect rituals, `data/neovitae/data_maps/neovitae/imperfect_ritual/imperfect_ritual_stats.json`:

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

`data/neovitae/tags/block/altar/runes.json`:

```json
{
  "replace": false,
  "values": [
    "minecraft:crying_obsidian",
    "#forge:storage_blocks/amethyst"
  ]
}
```
