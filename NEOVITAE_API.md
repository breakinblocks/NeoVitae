# Neo Vitae API Documentation

This document describes the Neo Vitae API for NeoForge 1.21.1. The API allows addon mods to interact with Neo Vitae's core systems including Soul Networks, Blood Altars, Rituals, and Living Armor.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Core API](#core-api)
3. [Soul Network System](#soul-network-system)
4. [Blood Altar System](#blood-altar-system)
5. [Altar Rune System](#altar-rune-system)
6. [Ritual System](#ritual-system)
7. [Sigil System](#sigil-system)
8. [Living Armor System](#living-armor-system)
9. [Events](#events)
10. [Registry Keys](#registry-keys)

---

## Getting Started

### Adding the API Dependency

Add Neo Vitae as a dependency in your `build.gradle`:

```groovy
repositories {
    // Add Neo Vitae maven here when available
}

dependencies {
    compileOnly("com.breakinblocks.neovitae:neovitae-api:VERSION")
}
```

### Accessing the API

The Neo Vitae API is accessed through the static `NeoVitaeAPI` class:

```java
import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.INeoVitaeAPI;

// Check if Neo Vitae is loaded
if (NeoVitaeAPI.isAvailable()) {
    INeoVitaeAPI api = NeoVitaeAPI.get();
    // Use the API...
}
```

---

## Core API

### INeoVitaeAPI

The main entry point interface for all Neo Vitae API operations.

**Package:** `com.breakinblocks.neovitae.api`

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getSoulNetwork(UUID uuid)` | `ISoulNetwork` | Gets the soul network for a player by UUID |
| `getSoulNetwork(Player player)` | `ISoulNetwork` | Gets the soul network for a player |
| `getSoulNetwork(String uuid)` | `ISoulNetwork` | Gets the soul network by UUID string |
| `getLivingArmorManager()` | `ILivingArmorManager` | Gets the Living Armor manager |
| `getRuneRegistry()` | `IAltarRuneRegistry` | Gets the Altar Rune registry |
| `getApiVersion()` | `String` | Gets the API version string |

#### Example

```java
INeoVitaeAPI api = NeoVitaeAPI.get();

// Get a player's soul network
ISoulNetwork network = api.getSoulNetwork(player);
if (network != null) {
    int currentLP = network.getCurrentEssence();
    System.out.println("Player has " + currentLP + " LP");
}

// Get Living Armor manager
ILivingArmorManager armorManager = api.getLivingArmorManager();
if (armorManager.hasFullSet(player)) {
    List<UpgradeInfo> upgrades = armorManager.getUpgrades(player);
}
```

---

## Soul Network System

The Soul Network stores Life Points (LP) that power Neo Vitae items and rituals. Each player has their own network.

### ISoulNetwork

**Package:** `com.breakinblocks.neovitae.api.soul`

Represents a player's Soul Network.

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPlayerId()` | `UUID` | Gets the owner's UUID |
| `getCurrentEssence()` | `int` | Gets current LP amount |
| `add(SoulTicket, int maximum)` | `int` | Adds LP up to maximum, returns amount added |
| `set(SoulTicket, int maximum)` | `int` | Sets LP to ticket amount, returns new value |
| `syphon(SoulTicket)` | `int` | Removes LP, returns amount removed |
| `hurtPlayer(Player, float)` | `void` | Damages player based on LP debt |
| `syphonAndDamage(Player, SoulTicket)` | `SyphonResult` | Syphons LP, damages player if insufficient |

#### SyphonResult Record

```java
record SyphonResult(boolean success, int amount) {
    public static SyphonResult failure();
    public static SyphonResult of(boolean success, int amount);
}
```

### SoulTicket

**Package:** `com.breakinblocks.neovitae.api.soul`

Represents an LP transaction with auditing information.

#### Factory Methods

| Method | Description |
|--------|-------------|
| `block(Level, BlockPos, int)` | For block-based operations (altars, ritual stones) |
| `block(Level, BlockPos)` | Block operation with zero amount |
| `item(ItemStack, int)` | For item-based operations (sigils, orbs) |
| `item(ItemStack, Level, BlockPos, int)` | Item operation with location context |
| `item(ItemStack, Level, Entity, int)` | Item operation with entity context |
| `command(CommandSource, String, int)` | For command-based operations |
| `create(int)` | Simple ticket with just an amount |

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getDescription()` | `Component` | Gets the audit description |
| `getAmount()` | `int` | Gets the LP amount |
| `isSyphon()` | `boolean` | True if this is a removal operation |

#### Example

```java
// Consume LP from a sigil item
ISoulNetwork network = api.getSoulNetwork(player);
SoulTicket ticket = SoulTicket.item(sigil, 100);
ISoulNetwork.SyphonResult result = network.syphonAndDamage(player, ticket);

if (result.success()) {
    // LP was consumed (or player took damage)
    performSigilEffect();
}

// Add LP from an altar
SoulTicket addTicket = SoulTicket.block(level, altarPos, 500);
int added = network.add(addTicket, 10000); // 10000 max capacity
```

---

## Blood Altar System

The Blood Altar is the core crafting mechanic in Neo Vitae.

### Accessing the Blood Altar

Neo Vitae provides a capability for accessing altar functionality:

```java
import com.breakinblocks.neovitae.api.capability.BMCapabilities;
import com.breakinblocks.neovitae.api.altar.IBloodAltar;

// Get altar capability from a block position
IBloodAltar altar = level.getCapability(BMCapabilities.BLOOD_ALTAR, pos, null);
if (altar != null) {
    int blood = altar.getCurrentBlood();
    int capacity = altar.getCapacity();
    int tier = altar.getTier();
}
```

### BMCapabilities

**Package:** `com.breakinblocks.neovitae.api.capability`

Block capabilities provided by Neo Vitae.

| Capability | Type | Description |
|------------|------|-------------|
| `BLOOD_ALTAR` | `BlockCapability<IBloodAltar, Direction>` | Access altar state and stats |

### IBloodAltar

**Package:** `com.breakinblocks.neovitae.api.altar`

Interface for Blood Altar block entities.

#### State Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getTier()` | `int` | Gets altar tier (0-5) |
| `getCurrentBlood()` | `int` | Gets current LP stored |
| `getCapacity()` | `int` | Gets maximum LP capacity |
| `isActive()` | `boolean` | True if crafting or filling |
| `canFill()` | `boolean` | True if can accept player sacrifice |

#### Crafting Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getProgressFloat()` | `float` | Progress as percentage (0.0-1.0) |
| `getCurrentRecipe()` | `BloodAltarRecipe` | Current recipe or null |
| `getLiquidRequired()` | `int` | LP required for current recipe |
| `getTotalCraftingTime()` | `int` | Total craft time in ticks |
| `getCraftingProgress()` | `int` | Current progress in ticks |

#### Rate Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getConsumptionRate()` | `int` | LP consumed per tick when crafting |
| `getDrainRate()` | `int` | LP drained from players per tick |
| `getChargingRate()` | `int` | LP charged to orbs per tick |
| `getChargingFrequency()` | `int` | Ticks between charge operations |

#### Bonus Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getBonusCapacity()` | `float` | Bonus capacity percentage |
| `getEfficiency()` | `float` | Efficiency multiplier |
| `getSelfSacrificeBonus()` | `float` | Self-sacrifice LP bonus |
| `getSacrificeBonus()` | `float` | Mob sacrifice LP bonus |

#### Utility Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getStackInSlot()` | `ItemStack` | Item in the altar |
| `getFluidHandler()` | `IFluidHandler` | Fluid handler for LP |
| `getBlockPos()` | `BlockPos` | Altar position |
| `getLevel()` | `Level` | World the altar is in |
| `checkTier()` | `void` | Forces tier recalculation |

#### Altar Tiers

| Tier | Description | Runes Required |
|------|-------------|----------------|
| 0 | Basic altar | None |
| 1 | Tier 1 | 8 runes in ring |
| 2 | Tier 2 | Expanded structure |
| 3 | Tier 3 | Advanced structure |
| 4 | Tier 4 | Complex structure |
| 5 | Tier 5 | Master structure |

### BloodAltarRecipe

**Package:** `com.breakinblocks.neovitae.api.recipe`

Abstract base class for Blood Altar recipes. Transform items using Life Essence (LP) at various altar tiers.

#### Constructors

```java
// Standard constructor (no component transfer)
BloodAltarRecipe(Ingredient input, ItemStack result, int minTier,
                 int totalBlood, int craftSpeed, int drainSpeed)

// Constructor with component transfer option
BloodAltarRecipe(Ingredient input, ItemStack result, int minTier,
                 int totalBlood, int craftSpeed, int drainSpeed,
                 boolean copyInputComponents)
```

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getInput()` | `Ingredient` | The input ingredient |
| `getResult()` | `ItemStack` | The base output item (copy) |
| `getMinTier()` | `int` | Minimum altar tier required (0-5) |
| `getTotalBlood()` | `int` | Total LP cost |
| `getCraftSpeed()` | `int` | LP consumed per tick while crafting |
| `getDrainSpeed()` | `int` | Progress lost per tick when out of LP |
| `shouldCopyInputComponents()` | `boolean` | Whether input components transfer to output |
| `assemble(BloodAltarInput, Provider)` | `ItemStack` | Assembles output with component transfer |

#### Component Transfer

Recipes can optionally copy data components from the input item to the output. This is useful when:

- **Bound items** - Preserve binding data through crafting
- **Enchanted items** - Keep enchantments when upgrading
- **Custom mod data** - Transfer any component-based data

When `copyInputComponents` is true, the `assemble()` method applies the input's components as a patch to the output, preserving the output's base components while adding/overwriting with input components.

#### JSON Format

```json
{
  "type": "neovitae:blood_altar_recipe",
  "input": {"item": "minecraft:diamond_sword"},
  "output": {"id": "neovitae:bound_sword"},
  "minTier": 2,
  "bloodNeeded": 5000,
  "craftSpeed": 10,
  "drainSpeed": 2,
  "copyInputComponents": true
}
```

| Field | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| `input` | Ingredient | Yes | - | Input item/tag |
| `output` | ItemStack | Yes | - | Output item (can include components) |
| `minTier` | int | Yes | - | Minimum altar tier (0-5) |
| `bloodNeeded` | int | Yes | - | Total LP required |
| `craftSpeed` | int | Yes | - | LP per tick consumption |
| `drainSpeed` | int | Yes | - | Progress loss per tick |
| `copyInputComponents` | boolean | No | false | Copy input components to output |

#### Example: Programmatic Recipe Creation

```java
// Recipe that preserves enchantments from input sword
BloodAltarRecipe recipe = new BloodAltarRecipe(
    Ingredient.of(Items.DIAMOND_SWORD),
    new ItemStack(BMItems.BOUND_SWORD),
    2,      // minTier
    5000,   // totalBlood
    10,     // craftSpeed
    2,      // drainSpeed
    true    // copyInputComponents - transfers enchantments, etc.
);
```

#### Example: Datagen with AltarRecipeBuilder

```java
AltarRecipeBuilder.build(BMItems.BOUND_SWORD)
    .from(Items.DIAMOND_SWORD)
    .minTier(2)
    .bloodNeeded(5000)
    .consumption(10)
    .drain(2)
    .copyInputComponents()  // Enable component transfer
    .save(output, "bound_sword");
```

### BloodAltarInput

**Package:** `com.breakinblocks.neovitae.api.recipe`

Recipe input for Blood Altar matching.

```java
public class BloodAltarInput implements RecipeInput {
    public BloodAltarInput(ItemStack inputStack, int altarTier);
    public int getAltarTier();
}
```

---

## Altar Rune System

The Altar Rune System allows addon mods to create custom rune types that affect Blood Altar behavior.

### Overview

Neo Vitae includes built-in rune types (Speed, Sacrifice, Capacity, etc.) that modify altar statistics. The API allows you to:

1. **Create custom rune types** - Define new rune behaviors
2. **Register rune blocks** - Associate blocks with rune types
3. **Modify altar stats** - Hook into stat calculation via events

### IAltarRuneType

**Package:** `com.breakinblocks.neovitae.api.altar.rune`

Interface for custom rune types that addon mods implement.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getId()` | `ResourceLocation` | Unique identifier (e.g., "mymod:mana_rune") |
| `getSerializedName()` | `String` | Serialized name for data files |

#### Example Implementation

```java
public class ManaRuneType implements IAltarRuneType {
    public static final ResourceLocation ID =
        ResourceLocation.fromNamespaceAndPath("mymod", "mana_rune");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public String getSerializedName() {
        return "mana_rune";
    }
}
```

### EnumAltarRuneType

**Package:** `com.breakinblocks.neovitae.api.altar.rune`

Built-in Blood Altar rune types.

| Value | Description |
|-------|-------------|
| `SPEED` | Increases LP consumption rate during crafting |
| `SACRIFICE` | Increases LP gained from mob sacrifice |
| `SELF_SACRIFICE` | Increases LP gained from player self-sacrifice |
| `DISPLACEMENT` | Increases fluid I/O rate for piping |
| `CAPACITY` | Increases altar blood capacity (additive) |
| `AUGMENTED_CAPACITY` | Increases altar blood capacity (multiplicative) |
| `ORB` | Increases soul network capacity bonus when filling orbs |
| `ACCELERATION` | Reduces ticks between altar operations |
| `CHARGING` | Enables pre-charging LP for instant crafting |
| `EFFICIENCY` | Reduces LP loss when altar runs out mid-craft |

### IAltarRuneRegistry

**Package:** `com.breakinblocks.neovitae.api.altar.rune`

Unified registry for all rune types (both built-in and custom) and block associations.

> **Note:** As of the unified rune system, both built-in (`EnumAltarRuneType`) and custom rune types are managed through this single registry. The built-in rune blocks are registered during mod common setup.

#### Rune Type Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `registerRuneType(IAltarRuneType)` | `void` | Registers a custom rune type |
| `getRuneType(ResourceLocation)` | `IAltarRuneType` | Gets rune type by ID |
| `getRuneTypeByName(String)` | `IAltarRuneType` | Gets rune type by serialized name |
| `getAllRuneTypes()` | `Collection<IAltarRuneType>` | All registered types (includes built-in) |
| `isRegistered(ResourceLocation)` | `boolean` | Checks if a rune type is registered |

#### Block Registration Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `registerRuneBlock(Block, IAltarRuneType, int)` | `void` | Associates block with rune type and amount |
| `getRunesForBlock(Block)` | `Map<IAltarRuneType, Integer>` | All runes provided by a block |
| `hasRunes(Block)` | `boolean` | True if block provides any runes |
| `getRuneAmount(Block, IAltarRuneType)` | `int` | Amount of specific rune from block |
| `hasRuneType(Block, IAltarRuneType)` | `boolean` | True if block provides specific rune type |

#### Example Usage

```java
// In your mod initialization
public void onCommonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
        IAltarRuneRegistry registry = NeoVitaeAPI.get().getRuneRegistry();

        // Register custom rune type
        registry.registerRuneType(new ManaRuneType());

        // Associate your block with the rune type
        // When placed in altar structure, provides 1 rune
        registry.registerRuneBlock(MY_MANA_RUNE_BLOCK.get(),
            new ManaRuneType(), 1);
    });
}
```

### AltarRuneModifiers

**Package:** `com.breakinblocks.neovitae.api.altar.rune`

Mutable container for altar modifier values passed to events.

#### Getter Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getCapacityMod()` | `float` | Bonus capacity percentage |
| `getTickRate()` | `int` | Ticks between operations |
| `getConsumptionMod()` | `float` | LP consumption multiplier |
| `getSacrificeMod()` | `float` | Mob sacrifice bonus |
| `getSelfSacrificeMod()` | `float` | Self-sacrifice bonus |
| `getDislocationMod()` | `float` | Fluid I/O rate bonus |
| `getOrbCapacityMod()` | `float` | Orb capacity bonus |
| `getChargeAmountMod()` | `float` | Charge amount multiplier |
| `getChargeCapacityMod()` | `float` | Charge capacity bonus |
| `getEfficiencyMod()` | `float` | Efficiency multiplier |

#### Modifier Methods

All modifier methods return `this` for chaining.

| Method | Description |
|--------|-------------|
| `addCapacityMod(float)` | Add to capacity bonus |
| `setTickRate(int)` | Set tick rate |
| `addConsumptionMod(float)` | Add to consumption multiplier |
| `addSacrificeMod(float)` | Add to sacrifice bonus |
| `addSelfSacrificeMod(float)` | Add to self-sacrifice bonus |
| `addDislocationMod(float)` | Add to I/O rate bonus |
| `addOrbCapacityMod(float)` | Add to orb capacity bonus |
| `addChargeAmountMod(float)` | Add to charge amount |
| `addChargeCapacityMod(float)` | Add to charge capacity |
| `addEfficiencyMod(float)` | Add to efficiency |

### RuneInstance

**Package:** `com.breakinblocks.neovitae.api.altar.rune`

Represents a single rune found during altar structure scanning. This record provides addon mods with direct access to scanned rune data, eliminating the need to re-scan the altar structure.

**This is especially useful for dynamic runes** - runes whose bonuses depend on their internal state (e.g., a rune that provides different bonuses based on stored power or mana).

#### Record Fields

| Field | Type | Description |
|-------|------|-------------|
| `pos` | `BlockPos` | Position of the rune block in the world |
| `block` | `Block` | The block at this position |
| `blockEntity` | `@Nullable BlockEntity` | The block entity at this position, or null |

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `hasBlockEntity()` | `boolean` | True if a block entity exists at this position |
| `isBlockEntityType(Class)` | `boolean` | True if block entity is instance of given type |
| `getBlockEntityAs(Class<T>)` | `T` | Cast block entity to type, or null if not that type |
| `isBlockType(Class)` | `boolean` | True if the block is instance of given type |

#### Example: Dynamic Rune State

```java
@SubscribeEvent
public void onCalculateStats(AltarRuneEvent.CalculateStats event) {
    // Iterate all rune instances to find our custom runes
    for (RuneInstance instance : event.getRuneInstances()) {
        // Check if this rune is our custom block entity
        if (instance.blockEntity() instanceof MyManaRuneBlockEntity manaRune) {
            // Apply bonus based on rune's internal state
            if (manaRune.hasMana()) {
                event.getModifiers().addConsumptionMod(0.15f);
            } else {
                // Unpowered penalty
                event.getModifiers().addConsumptionMod(-0.10f);
            }
        }
    }
}
```

### AltarRuneEvent

**Package:** `com.breakinblocks.neovitae.api.event`

Events fired when the Blood Altar calculates or applies rune effects. These events use a unified rune map that includes both built-in and custom rune types.

**Event Order:**
1. **GatherRunes** - Fired after scanning, allows adding virtual runes
2. **CalculateStats** - Fired during stat calculation, allows modifying bonuses
3. **PostCalculate** - Fired after stats are finalized, informational only

#### Base Event Methods

All three event types inherit these methods:

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getAltar()` | `IBloodAltar` | The Blood Altar instance |
| `getLevel()` | `Level` | The world level |
| `getPos()` | `BlockPos` | The altar's position |
| `getTier()` | `int` | The altar's current tier |

#### AltarRuneEvent.GatherRunes

Fired when scanning for runes. Allows adding virtual runes not from blocks.

```java
@SubscribeEvent
public static void onGatherRunes(AltarRuneEvent.GatherRunes event) {
    // Add runes using the unified method (works for any type)
    event.addRunes(EnumAltarRuneType.SPEED, 2);
    event.addRunes(MyMod.MY_RUNE_TYPE, 1);

    // Access scanned rune instances (read-only during GatherRunes)
    for (RuneInstance instance : event.getRuneInstances()) {
        // Inspect what runes were found
    }
}
```

| Method | Return Type | Description |
|--------|-------------|-------------|
| `addRunes(IAltarRuneType, int)` | `void` | Add any rune type (preferred) |
| `getRuneCounts()` | `Map<IAltarRuneType, Integer>` | Mutable map of all rune counts |
| `getRuneInstances()` | `List<RuneInstance>` | Read-only list of scanned rune instances |

#### AltarRuneEvent.CalculateStats

Fired during stat calculation. Modify the `AltarRuneModifiers` here. **This is the primary event for dynamic rune logic.**

```java
@SubscribeEvent
public static void onCalculateStats(AltarRuneEvent.CalculateStats event) {
    // Get rune count by type
    int speedCount = event.getRuneCount(EnumAltarRuneType.SPEED);
    int myRuneCount = event.getRuneCount(MyMod.MY_RUNE_TYPE);

    // Modify stats based on rune counts
    if (myRuneCount > 0) {
        event.getModifiers().addCapacityMod(0.1f * myRuneCount);
    }

    // Access dynamic rune block entities directly (no rescanning needed!)
    List<MyRuneBlockEntity> myRunes = event.getRuneBlockEntities(MyRuneBlockEntity.class);
    for (MyRuneBlockEntity rune : myRunes) {
        if (rune.isPowered()) {
            event.getModifiers().addConsumptionMod(0.15f);
        }
    }
}
```

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getModifiers()` | `AltarRuneModifiers` | Mutable modifiers to change |
| `getRuneCount(IAltarRuneType)` | `int` | Count of a specific rune type |
| `getRuneCounts()` | `Map<IAltarRuneType, Integer>` | Read-only map of all rune counts |
| `getRuneInstances()` | `List<RuneInstance>` | All scanned rune instances |
| `getRuneBlockEntities(Class<T>)` | `List<T>` | **Filter to block entities of type T** |
| `getRuneInstancesByType(IAltarRuneType)` | `List<RuneInstance>` | Filter instances by rune type |

#### AltarRuneEvent.PostCalculate

Fired after all modifications applied. Informational only.

```java
@SubscribeEvent
public static void onPostCalculate(AltarRuneEvent.PostCalculate event) {
    // Log final stats for debugging
    AltarRuneModifiers finals = event.getFinalModifiers();
    LOGGER.debug("Altar at {} has {}% capacity bonus",
        event.getPos(), finals.getCapacityMod() * 100);

    // Access rune instances for post-calculation effects
    for (RuneInstance instance : event.getRuneInstances()) {
        // Trigger visual effects, particles, etc.
    }
}
```

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getFinalModifiers()` | `AltarRuneModifiers` | The finalized modifiers |
| `getRuneInstances()` | `List<RuneInstance>` | All scanned rune instances |

### Complete Custom Rune Example

This example shows a dynamic mana rune that provides different bonuses based on whether it has stored mana.

```java
// 1. Define your rune type
public class ManaRuneType implements IAltarRuneType {
    public static final ManaRuneType INSTANCE = new ManaRuneType();
    public static final ResourceLocation ID =
        ResourceLocation.fromNamespaceAndPath("mymod", "mana_rune");

    @Override
    public ResourceLocation getId() { return ID; }

    @Override
    public String getSerializedName() { return "mana_rune"; }
}

// 2. Create your rune block entity (for dynamic behavior)
public class ManaRuneBlockEntity extends BlockEntity {
    private int storedMana = 0;

    public boolean hasMana() { return storedMana > 0; }
    public int getStoredMana() { return storedMana; }
    // ... mana storage logic
}

// 3. Create your rune block
public class ManaRuneBlock extends Block implements EntityBlock {
    public ManaRuneBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ManaRuneBlockEntity(ModBlockEntities.MANA_RUNE.get(), pos, state);
    }
}

// 4. Register in common setup
@Mod.EventBusSubscriber(modid = "mymod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSetup {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            IAltarRuneRegistry registry = NeoVitaeAPI.get().getRuneRegistry();
            registry.registerRuneType(ManaRuneType.INSTANCE);
            registry.registerRuneBlock(ModBlocks.MANA_RUNE.get(),
                ManaRuneType.INSTANCE, 1);
        });
    }
}

// 5. Handle dynamic stat calculation - NO RESCANNING NEEDED!
@Mod.EventBusSubscriber(modid = "mymod", bus = Mod.EventBusSubscriber.Bus.GAME)
public class RuneEvents {
    @SubscribeEvent
    public static void onCalculateStats(AltarRuneEvent.CalculateStats event) {
        // Use getRuneBlockEntities() to find all our rune block entities
        // Neo Vitae already scanned the altar - we just filter the results!
        List<ManaRuneBlockEntity> manaRunes =
            event.getRuneBlockEntities(ManaRuneBlockEntity.class);

        int poweredCount = 0;
        int unpoweredCount = 0;

        for (ManaRuneBlockEntity rune : manaRunes) {
            if (rune.hasMana()) {
                poweredCount++;
            } else {
                unpoweredCount++;
            }
        }

        // Powered runes: +20% capacity, +15% efficiency
        if (poweredCount > 0) {
            event.getModifiers()
                .addCapacityMod(0.20f * poweredCount)
                .addEfficiencyMod(0.15f * poweredCount);
        }

        // Unpowered runes: still +5% capacity (weaker bonus)
        if (unpoweredCount > 0) {
            event.getModifiers()
                .addCapacityMod(0.05f * unpoweredCount);
        }
    }
}
```

### Before vs After: Why RuneInstance Matters

**Before (required rescanning 15,000+ blocks):**
```java
// Old approach - INEFFICIENT, had to scan the entire altar area!
@SubscribeEvent
public void onCalculateStats(AltarRuneEvent.CalculateStats event) {
    BlockPos altarPos = event.getPos();
    Level level = event.getLevel();

    // Manually scan a 27x21x27 area to find our runes
    for (int x = -13; x <= 13; x++) {
        for (int y = -5; y <= 15; y++) {
            for (int z = -13; z <= 13; z++) {
                BlockPos checkPos = altarPos.offset(x, y, z);
                BlockEntity be = level.getBlockEntity(checkPos);
                if (be instanceof MyRuneBlockEntity myRune) {
                    // Finally found it after checking thousands of blocks!
                    applyBonus(myRune, event.getModifiers());
                }
            }
        }
    }
}
```

**After (direct access, no rescanning):**
```java
// New approach - Neo Vitae provides the rune instances directly!
@SubscribeEvent
public void onCalculateStats(AltarRuneEvent.CalculateStats event) {
    // One line to get all matching block entities
    List<MyRuneBlockEntity> myRunes = event.getRuneBlockEntities(MyRuneBlockEntity.class);

    for (MyRuneBlockEntity rune : myRunes) {
        applyBonus(rune, event.getModifiers());
    }
}
```

---

## Ritual System

Neo Vitae has two types of rituals:

- **Rituals**: Complex multiblock structures with ongoing effects
- **Imperfect Rituals**: Simple one-time effects

### IRitual

**Package:** `com.breakinblocks.neovitae.api.ritual`

Interface for multiblock rituals.

#### Core Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `performRitual(IMasterRitualStone)` | `void` | Called each refresh to perform effect |
| `getRefreshCost()` | `int` | LP cost per refresh |
| `getRefreshTime()` | `int` | Ticks between refreshes |
| `gatherComponents(Consumer<RitualComponent>)` | `void` | Defines rune structure |
| `getNewCopy()` | `IRitual` | Creates fresh instance |

#### Lifecycle Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `activateRitual(IMasterRitualStone, Player, UUID)` | `boolean` | Called on activation |
| `stopRitual(IMasterRitualStone, BreakType)` | `void` | Called when stopped |

#### Info Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getName()` | `String` | Unique ritual name |
| `getCrystalLevel()` | `int` | Required crystal tier (1=weak, 2=awakened) |
| `getActivationCost()` | `int` | LP to activate |
| `getTranslationKey()` | `String` | Translation key |

#### Range Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getBlockRange(String)` | `AreaDescriptor` | Gets named area |
| `getListOfRanges()` | `List<String>` | All modifiable range keys |
| `getModifiableRanges()` | `Map<String, AreaDescriptor>` | All ranges |

#### BreakType Enum

| Value | Description |
|-------|-------------|
| `DEACTIVATE` | Player deactivated |
| `BREAK_MRS` | Master stone broken |
| `BREAK_STONE` | Ritual stone broken |
| `ACTIVATE` | Another ritual activated |
| `REDSTONE` | Redstone signal stopped it |
| `EXPLOSION` | Destroyed by explosion |

### IMasterRitualStone

**Package:** `com.breakinblocks.neovitae.api.ritual`

Interface for Master Ritual Stone block entities.

#### State Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getLevel()` | `Level` | World the stone is in |
| `getBlockPos()` | `BlockPos` | Position of the stone |
| `getOwner()` | `UUID` | Owner's UUID |
| `setOwner(UUID)` | `void` | Sets owner |
| `getCurrentRitual()` | `IRitual` | Active ritual or null |
| `isActive()` | `boolean` | True if ritual running |
| `getDirection()` | `Direction` | Facing direction |
| `isInverted()` | `boolean` | True if inverted |

#### Timing Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getCooldown()` | `int` | Current cooldown ticks |
| `setCooldown(int)` | `void` | Sets cooldown |
| `getRunningTime()` | `long` | Total running time |

#### Control Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `activateRitual(IRitual, Player, int)` | `boolean` | Activates a ritual |
| `performRitual()` | `void` | Performs current ritual |
| `stopRitual(BreakType)` | `void` | Stops current ritual |
| `checkStructure(IRitual)` | `boolean` | Validates rune structure |

#### Range Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getBlockRange(String)` | `AreaDescriptor` | Gets named range |
| `getBlockRanges()` | `Map<String, AreaDescriptor>` | All ranges |
| `setBlockRange(String, AreaDescriptor)` | `void` | Sets a range |
| `setBlockRanges(Map)` | `void` | Sets all ranges |

#### Network Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `ticket()` | `SoulTicket` | Creates soul ticket for this stone |
| `ticket(int)` | `SoulTicket` | Creates ticket with amount |
| `getOwnerNetwork()` | `ISoulNetwork` | Gets owner's network |
| `notifyOwner(Component)` | `void` | Sends message to owner |

### IImperfectRitual

**Package:** `com.breakinblocks.neovitae.api.ritual`

Interface for simple one-time rituals.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `onActivate(IImperfectRitualStone, Player)` | `boolean` | Performs the ritual |
| `getName()` | `String` | Unique name |
| `getBlockRequirement()` | `Predicate<BlockState>` | Required block above stone |
| `getActivationCost()` | `int` | LP cost |
| `isLightShow()` | `boolean` | Show lightning effect |
| `getTranslationKey()` | `String` | Translation key |

### IImperfectRitualStone

**Package:** `com.breakinblocks.neovitae.api.ritual`

Interface for Imperfect Ritual Stone block entities.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getRitualWorld()` | `Level` | World the stone is in |
| `getRitualPos()` | `BlockPos` | Position of the stone |

### RitualComponent

**Package:** `com.breakinblocks.neovitae.api.ritual`

Represents a single rune in a ritual structure.

```java
public record RitualComponent(BlockPos offset, EnumRuneType runeType) {
    public RitualComponent(int x, int y, int z, EnumRuneType runeType);
    public int getX();
    public int getY();
    public int getZ();
    public BlockPos getBlockPos(BlockPos masterPos);
}
```

### EnumRuneType

**Package:** `com.breakinblocks.neovitae.api.ritual`

Types of ritual runes.

| Value | Color | Description |
|-------|-------|-------------|
| `BLANK` | Gray | Basic rune |
| `WATER` | Aqua | Water elemental |
| `FIRE` | Red | Fire elemental |
| `EARTH` | Green | Earth elemental |
| `AIR` | White | Air elemental |
| `DUSK` | Dark Gray | Advanced rune |
| `DAWN` | Gold | Most powerful rune |

### AreaDescriptor

**Package:** `com.breakinblocks.neovitae.api.ritual`

Abstract class for defining ritual areas of effect.

#### Abstract Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `resetCache()` | `void` | Clears cached positions |
| `isWithinArea(BlockPos)` | `boolean` | Checks if position is in area |
| `getContainedPositions(BlockPos)` | `List<BlockPos>` | All positions in area |
| `getAABB(BlockPos)` | `AABB` | Bounding box |
| `modifyAreaByBlockPositions(BlockPos, BlockPos)` | `void` | Modifies bounds |
| `isWithinRange(BlockPos, BlockPos, int, int)` | `boolean` | Checks limits |
| `saveToNBT(CompoundTag)` | `void` | Saves to NBT |
| `loadFromNBT(CompoundTag)` | `void` | Loads from NBT |
| `copy()` | `AreaDescriptor` | Creates copy |
| `intersects(AreaDescriptor)` | `boolean` | Checks intersection |
| `offset(BlockPos)` | `AreaDescriptor` | Returns offset copy |

#### Implementations

##### Rectangle

Rectangular box-shaped area.

```java
// Create with corners
new Rectangle(minOffset, maxOffset);

// Create with position and size
new Rectangle(offset, sizeX, sizeY, sizeZ);

// Create centered rectangle
Rectangle.createCenteredAt(center, radius, height);
```

##### HemiSphere

Hemispherical area (half sphere above a point).

```java
new HemiSphere(centerOffset, radius);
```

##### Cross

Plus-shaped area extending in cardinal directions.

```java
new Cross(centerOffset, armLength, height);
```

---

## Sigil System

Sigils are Neo Vitae items that provide various effects powered by Life Points (LP). The sigil system is fully datapack-driven, allowing addon mods to create custom sigil effects.

### Overview

Neo Vitae's sigil system consists of:

1. **Sigil Effects** - The actual behavior/logic (implemented via `ISigilEffect`)
2. **Sigil Types** - Datapack-defined configurations that combine effects with LP costs
3. **Sigil Items** - Items that reference a sigil type

### ISigilEffect

**Package:** `com.breakinblocks.neovitae.api.sigil`

Interface for implementing custom sigil effects.

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `codec()` | `MapCodec<? extends ISigilEffect>` | Returns the codec for serialization |
| `useOnAir(Level, Player, ItemStack)` | `boolean` | Called when right-clicking air |
| `useOnBlock(Level, Player, ItemStack, BlockPos, Direction, Vec3)` | `boolean` | Called when right-clicking a block |
| `useOnEntity(Level, Player, ItemStack, Entity)` | `boolean` | Called when right-clicking an entity |
| `activeTick(Level, Player, ItemStack, int, boolean)` | `void` | Called every tick while active (toggleable sigils) |
| `isToggleable()` | `boolean` | Whether this effect can be toggled on/off |

All methods except `codec()` have default implementations that return `false` or do nothing.

#### Effect Types

Sigils can be:

- **Static** - Single-use effects triggered by right-click (e.g., Water Sigil places water)
- **Toggleable** - Ongoing effects that drain LP while active (e.g., Air Sigil provides flight)
- **Hybrid** - Both toggled effects and right-click actions (e.g., Green Grove with bone meal)

### Creating Custom Sigil Effects

#### Step 1: Implement ISigilEffect

```java
package mymod.sigil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;

public record MyCustomEffect(int power) implements ISigilEffect {

    public static final int DEFAULT_POWER = 5;

    // Codec for serialization - required for datapack loading
    public static final MapCodec<MyCustomEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("power", DEFAULT_POWER).forGetter(MyCustomEffect::power)
            ).apply(instance, MyCustomEffect::new)
    );

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) {
            return false;
        }

        // Your custom effect logic here
        player.heal(power);
        return true; // Return true to consume LP
    }

    @Override
    public boolean isToggleable() {
        return false; // Set to true for toggle-based sigils
    }
}
```

#### Step 2: Register the Effect Codec

```java
package mymod;

import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.api.registry.NeoVitaeRegistries;
import com.breakinblocks.neovitae.api.sigil.ISigilEffect;
import mymod.sigil.MyCustomEffect;

import java.util.function.Supplier;

public class MyModSigilEffects {

    public static final DeferredRegister<MapCodec<? extends ISigilEffect>> SIGIL_EFFECTS =
            DeferredRegister.create(NeoVitaeRegistries.SIGIL_EFFECT_TYPE_KEY, "mymod");

    public static final Supplier<MapCodec<MyCustomEffect>> MY_CUSTOM_EFFECT =
            SIGIL_EFFECTS.register("my_custom_effect", () -> MyCustomEffect.CODEC);

    public static void register(IEventBus modBus) {
        SIGIL_EFFECTS.register(modBus);
    }
}
```

#### Step 3: Create a Sigil Type JSON

Create a datapack file at `data/mymod/neovitae/sigil_type/my_custom_sigil.json`:

```json
{
  "lp_cost_air": 200,
  "lp_cost_block": 0,
  "lp_cost_entity": 0,
  "lp_cost_active": 0,
  "drain_interval": 100,
  "effect": {
    "type": "mymod:my_custom_effect",
    "power": 10
  }
}
```

#### JSON Sigil Type Fields

| Field | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| `lp_cost_air` | int | No | 0 | LP cost when used on air |
| `lp_cost_block` | int | No | 0 | LP cost when used on a block |
| `lp_cost_entity` | int | No | 0 | LP cost when used on an entity |
| `lp_cost_active` | int | No | 0 | LP cost per drain interval (toggleable) |
| `drain_interval` | int | No | 100 | Ticks between LP drain (5 seconds default) |
| `effect` | object | No | - | The effect implementation with type and parameters |

#### Step 4: Create the Sigil Item

```java
// In your item registration
public static final DeferredHolder<Item, SigilItem> MY_CUSTOM_SIGIL =
    ITEMS.register("my_custom_sigil", () ->
        new SigilItem(ResourceKey.create(
            NeoVitaeRegistries.SIGIL_TYPE_KEY,
            ResourceLocation.fromNamespaceAndPath("mymod", "my_custom_sigil")
        ))
    );
```

### Toggleable Sigil Example

For sigils with ongoing effects:

```java
public record MyToggleEffect(int range) implements ISigilEffect {

    public static final MapCodec<MyToggleEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("range", 5).forGetter(MyToggleEffect::range)
            ).apply(instance, MyToggleEffect::new)
    );

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean isToggleable() {
        return true; // Enable toggle functionality
    }

    @Override
    public void activeTick(Level level, Player player, ItemStack stack, int itemSlot, boolean isSelected) {
        if (level.isClientSide) {
            return;
        }

        // Effect runs every tick while sigil is active
        // For example, pull nearby items
        BlockPos playerPos = player.blockPosition();
        AABB area = new AABB(playerPos).inflate(range);

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, area)) {
            Vec3 motion = player.position().subtract(item.position()).normalize().scale(0.1);
            item.setDeltaMovement(item.getDeltaMovement().add(motion));
        }
    }
}
```

With corresponding JSON:

```json
{
  "lp_cost_active": 50,
  "drain_interval": 20,
  "effect": {
    "type": "mymod:my_toggle_effect",
    "range": 8
  }
}
```

### Built-in Sigil Effects

Neo Vitae includes these effect types:

| Effect Type | Description | Toggleable |
|------------|-------------|------------|
| `neovitae:air` | Provides creative flight | Yes |
| `neovitae:place_fluid` | Places water or lava | No |
| `neovitae:void` | Voids fluids in an area | No |
| `neovitae:green_grove` | Accelerates crop growth, bone meal on use | Yes |
| `neovitae:fast_miner` | Grants haste effect | Yes |
| `neovitae:magnetism` | Pulls items and XP orbs | Yes |
| `neovitae:frost` | Freezes water below player | Yes |
| `neovitae:suppression` | Suppresses fluids in area | Yes |
| `neovitae:phantom_bridge` | Creates phantom blocks below player | Yes |
| `neovitae:divination` | Shows soul network info | No |
| `neovitae:blood_light` | Places light blocks | No |
| `neovitae:teleposition` | Teleports to bound teleposer | No |

---

## Living Armor System

Living Armor gains experience and levels up upgrades as the player performs actions.

### IUpgradeHolder

**Package:** `com.breakinblocks.neovitae.api.item`

Interface for items that can hold Living Armor upgrades. Neo Vitae's Living Armor pieces implement this interface.

Use this interface to:
- Detect Living Armor items via `instanceof IUpgradeHolder`
- Query upgrade-related information from armor pieces
- Check if a player has a complete Living Armor set

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getMaxUpgradePoints(ItemStack, Player)` | `int` | Gets maximum upgrade points available |
| `hasFullLivingArmorSet(Player)` | `boolean` | True if player has complete set |
| `isInvalidArmor(ItemStack)` | `boolean` | True if armor is dead/invalid |

#### Example Usage

```java
import com.breakinblocks.neovitae.api.item.IUpgradeHolder;

// Check if an item is Living Armor
ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
if (chestplate.getItem() instanceof IUpgradeHolder holder) {
    // Get upgrade point information
    int maxPoints = holder.getMaxUpgradePoints(chestplate, player);
    boolean hasFullSet = holder.hasFullLivingArmorSet(player);
    boolean isDead = holder.isInvalidArmor(chestplate);

    if (hasFullSet && !isDead) {
        // Player is wearing a valid full Living Armor set
        System.out.println("Max upgrade points: " + maxPoints);
    }
}
```

### ILivingArmorManager

**Package:** `com.breakinblocks.neovitae.api.living`

Manager for Living Armor operations.

#### Query Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `hasFullSet(Player)` | `boolean` | True if wearing full set |
| `getChestPiece(Player)` | `ItemStack` | Gets chest piece or EMPTY |
| `getUpgrades(Player)` | `List<UpgradeInfo>` | All upgrades on armor |
| `getUpgradeLevel(Player, ResourceLocation)` | `int` | Level of specific upgrade |
| `getUpgradeExperience(Player, ResourceLocation)` | `float` | Experience of upgrade |

#### Modification Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `grantUpgradeExperience(Player, ResourceLocation, float)` | `boolean` | Grants XP to upgrade |

#### Points Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getUsedUpgradePoints(Player)` | `int` | Points used |
| `getMaxUpgradePoints()` | `int` | Maximum available (100) |
| `getAvailableUpgradePoints(Player)` | `int` | Unused points |

#### UpgradeInfo Record

```java
record UpgradeInfo(
    ResourceLocation upgradeId,
    int level,
    float experience,
    int pointCost
) {}
```

### ILivingArmorUpgrade

**Package:** `com.breakinblocks.neovitae.api.living`

Interface for Living Armor upgrades.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getMaxLevel()` | `int` | Maximum upgrade level |
| `getLevelFromExp(float)` | `int` | Level at experience amount |
| `getExpForNextLevel(int)` | `float` | XP needed for next level |
| `getTotalExpForLevel(int)` | `float` | Total XP for level |
| `getPointCost(int)` | `int` | Point cost at level |
| `getEffects()` | `DataComponentMap` | Upgrade effects |

#### Example

```java
ILivingArmorManager manager = api.getLivingArmorManager();

if (manager.hasFullSet(player)) {
    // Check upgrade level
    ResourceLocation upgradeId = ResourceLocation.fromNamespaceAndPath("neovitae", "strong_legs");
    int level = manager.getUpgradeLevel(player, upgradeId);

    // Grant experience
    manager.grantUpgradeExperience(player, upgradeId, 10.0f);

    // Get all upgrades
    for (UpgradeInfo info : manager.getUpgrades(player)) {
        System.out.println(info.upgradeId() + " level " + info.level());
    }
}
```

---

## Events

Neo Vitae fires NeoForge events that addon mods can listen to.

### SoulNetworkEvent

**Package:** `com.breakinblocks.neovitae.api.event`

Events for LP transactions.

#### SoulNetworkEvent.PreSyphon (Cancellable)

Fired before LP is removed from a network.

```java
@SubscribeEvent
public void onPreSyphon(SoulNetworkEvent.PreSyphon event) {
    // Reduce syphon amount by 10%
    event.setModifiedAmount((int)(event.getAmount() * 0.9));

    // Or cancel entirely
    event.setCanceled(true);
}
```

#### SoulNetworkEvent.PostSyphon

Fired after LP is removed (not cancellable).

```java
@SubscribeEvent
public void onPostSyphon(SoulNetworkEvent.PostSyphon event) {
    int removed = event.getActualAmount();
    // Log or track LP usage
}
```

#### SoulNetworkEvent.PreAdd (Cancellable)

Fired before LP is added to a network.

```java
@SubscribeEvent
public void onPreAdd(SoulNetworkEvent.PreAdd event) {
    // Bonus 20% LP
    event.setModifiedAmount((int)(event.getAmount() * 1.2));
}
```

#### SoulNetworkEvent.PostAdd

Fired after LP is added (not cancellable).

### BloodAltarCraftEvent

**Package:** `com.breakinblocks.neovitae.api.event`

Events for Blood Altar crafting.

#### BloodAltarCraftEvent.Crafting (Cancellable)

Fired when craft is about to complete.

```java
@SubscribeEvent
public void onCrafting(BloodAltarCraftEvent.Crafting event) {
    // Modify output
    ItemStack output = event.getOutput();
    output.setCount(output.getCount() * 2);
    event.setOutput(output);

    // Or cancel (LP still consumed)
    event.setCanceled(true);
}
```

#### BloodAltarCraftEvent.Crafted

Fired after craft completes (not cancellable).

```java
@SubscribeEvent
public void onCrafted(BloodAltarCraftEvent.Crafted event) {
    // Achievement tracking, statistics, etc.
    IBloodAltar altar = event.getAltar();
    BloodAltarRecipe recipe = event.getRecipe();
}
```

### LivingArmorEvent

**Package:** `com.breakinblocks.neovitae.api.event`

Events for Living Armor upgrades.

#### LivingArmorEvent.ExperienceGain (Cancellable)

Fired when an upgrade gains experience.

```java
@SubscribeEvent
public void onExpGain(LivingArmorEvent.ExperienceGain event) {
    // Double XP for specific upgrade
    if (event.getUpgradeId().getPath().equals("strong_legs")) {
        event.setExperience(event.getExperience() * 2);
    }
}
```

#### LivingArmorEvent.LevelUp

Fired when an upgrade levels up (not cancellable).

```java
@SubscribeEvent
public void onLevelUp(LivingArmorEvent.LevelUp event) {
    Player player = event.getWearer();
    player.sendSystemMessage(Component.literal(
        "Upgrade " + event.getUpgradeId() +
        " leveled to " + event.getNewLevel()
    ));
}
```

---

## Registry Keys

**Package:** `com.breakinblocks.neovitae.api.registry`

Neo Vitae provides registry keys for addon mods to register custom content.

### NeoVitaeRegistries

| Key | Type | Description |
|-----|------|-------------|
| `RITUAL_KEY` | `ResourceKey<Registry<IRitual>>` | For multiblock rituals |
| `IMPERFECT_RITUAL_KEY` | `ResourceKey<Registry<IImperfectRitual>>` | For imperfect rituals |
| `SIGIL_EFFECT_TYPE_KEY` | `ResourceKey<Registry<MapCodec<? extends ISigilEffect>>>` | For custom sigil effects |

### Registering Custom Rituals

```java
// In your mod's registration class
public static final DeferredRegister<Ritual> RITUALS =
    DeferredRegister.create(NeoVitaeRegistries.RITUAL_KEY, "yourmodid");

public static final DeferredHolder<Ritual, MyCustomRitual> MY_RITUAL =
    RITUALS.register("my_ritual", MyCustomRitual::new);

// Don't forget to register the DeferredRegister to the mod bus
public YourMod(IEventBus modBus) {
    RITUALS.register(modBus);
}
```

### Registering Custom Imperfect Rituals

```java
public static final DeferredRegister<ImperfectRitual> IMPERFECT_RITUALS =
    DeferredRegister.create(NeoVitaeRegistries.IMPERFECT_RITUAL_KEY, "yourmodid");

public static final DeferredHolder<ImperfectRitual, MyImperfectRitual> MY_RITUAL =
    IMPERFECT_RITUALS.register("my_ritual", MyImperfectRitual::new);
```

---

## API Package Structure

```
com.breakinblocks.neovitae.api/
├── NeoVitaeAPI.java          # Static accessor
├── INeoVitaeAPI.java         # Main API interface
├── altar/
│   ├── IBloodAltar.java        # Blood Altar interface
│   └── rune/
│       ├── AltarRuneModifiers.java   # Mutable modifier container
│       ├── EnumAltarRuneType.java    # Built-in rune types
│       ├── IAltarRuneRegistry.java   # Custom rune registry
│       ├── IAltarRuneType.java       # Custom rune type interface
│       └── RuneInstance.java         # Rune position/block entity data
├── capability/
│   └── BMCapabilities.java     # Block capabilities (BLOOD_ALTAR)
├── event/
│   ├── AltarRuneEvent.java     # Rune calculation events (with RuneInstance access)
│   ├── BloodAltarCraftEvent.java
│   ├── LivingArmorEvent.java
│   └── SoulNetworkEvent.java
├── item/
│   └── IUpgradeHolder.java     # Living Armor item interface
├── living/
│   ├── ILivingArmorManager.java  # (includes UpgradeInfo record)
│   └── ILivingArmorUpgrade.java
├── recipe/
│   ├── BloodAltarInput.java
│   └── BloodAltarRecipe.java
├── registry/
│   └── NeoVitaeRegistries.java
├── ritual/
│   ├── AreaDescriptor.java     # + Rectangle, HemiSphere, Cross
│   ├── EnumRuneType.java
│   ├── IImperfectRitual.java
│   ├── IImperfectRitualStone.java
│   ├── IMasterRitualStone.java
│   ├── IRitual.java
│   └── RitualComponent.java
├── sigil/
│   └── ISigilEffect.java       # Custom sigil effect interface
└── soul/
    ├── ISoulNetwork.java
    ├── SoulTicket.java
    └── SyphonResult.java       # Syphon operation result
```

---

When reporting issues, please include:

- Neo Vitae version
- NeoForge version
- Relevant code snippets
- Full error logs
