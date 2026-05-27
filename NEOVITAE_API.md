# Neo Vitae API Documentation

This document describes the Neo Vitae API for NeoForge 1.21.1. The API allows addon mods to interact with Neo Vitae's core systems: Animas, Ara Vitaes, Altar Runes, Rituals, Sigils, Sentient Armor, Spiritus, Tranquility, Routing, and Stream effects.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Core API](#core-api)
3. [Anima System](#anima-system)
4. [Spiritus System](#spiritus-system)
5. [Ara Vitae System](#ara-vitae-system)
6. [Altar Rune System](#altar-rune-system)
7. [Ritual System](#ritual-system)
8. [Sigil System](#sigil-system)
9. [Sentient Armor System](#sentient-armor-system)
10. [Tranquility / Incense](#tranquility--incense)
11. [Routing System](#routing-system)
12. [Stream Effects](#stream-effects)
13. [Custom Player Attributes](#custom-player-attributes)
14. [Events](#events)
15. [Capabilities](#capabilities)
16. [Registry Keys](#registry-keys)
17. [API Package Structure](#api-package-structure)

---

## Getting Started

### Adding the API Dependency

Add Neo Vitae as a dependency in your `build.gradle`.

```groovy
repositories {
    maven {
        name = "BreakInBlocks"
        url = "https://maven.breakinblocks.com/releases"
    }
    // Modonomicon is a transitive dependency of NeoVitae
    maven {
        name = "KliKli's maven"
        url = "https://dl.cloudsmith.io/public/klikli-dev/mods/maven/"
        content { includeGroup "com.klikli_dev" }
    }
    // Geckolib is a transitive dependency of NeoVitae
    maven {
        name = "Geckolib"
        url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/"
        content { includeGroup "software.bernie.geckolib" }
    }
}

dependencies {
    implementation("com.breakinblocks.neovitae:neovitae:${minecraftVersion}-${neoVitaeVersion}")
}
```

You can find the version of the latest released artifact [here](https://maven.breakinblocks.com/#/releases/com/breakinblocks/neovitae/neovitae).

> **Note** The API classes are in the main mod JAR under `com.breakinblocks.neovitae.api`. There is no separate api artifact.

### Accessing the API

The Neo Vitae API is accessed through the static `NeoVitaeAPI` class.

```java
import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.INeoVitaeAPI;

INeoVitaeAPI api = NeoVitaeAPI.getInstance();
```

> **Note** Calling `getInstance()` before Neo Vitae has finished common setup throws `IllegalStateException`. It is safe from `FMLCommonSetupEvent` or later.

---

## Core API

### NeoVitaeAPI

**Package** `com.breakinblocks.neovitae.api`

Static accessor for the active `INeoVitaeAPI` implementation.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getInstance()` | `INeoVitaeAPI` | Returns the API instance; throws if not initialized |

### INeoVitaeAPI

**Package** `com.breakinblocks.neovitae.api`

The main entry point interface for all Neo Vitae API operations.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getAnima(UUID uuid)` | `@Nullable IAnima` | Gets the anima for a player by UUID |
| `getSentientArmorManager()` | `ISentientArmorManager` | Gets the Sentient Armor manager |
| `getRuneRegistry()` | `IAltarRuneRegistry` | Gets the Altar Rune registry |
| `getTranquilityHandler()` | `ITranquilityHandler` | Gets the tranquility lookup handler |
| `getSpiritusHandler()` | `ISpiritusHandler` | Gets the chunk-based spiritus aura handler |
| `getPlayerWillHandler()` | `IPlayerSpiritusHandler` | Gets the inventory-level spiritus item handler |
| `getApiVersion()` | `String` | Returns the API version string |

#### Example

```java
INeoVitaeAPI api = NeoVitaeAPI.getInstance();

IAnima anima = api.getAnima(player.getUUID());
if (anima != null) {
    int currentEV = anima.getCurrentEV();
}

ISentientArmorManager armor = api.getSentientArmorManager();
if (armor.hasFullSet(player)) {
    List<ISentientArmorManager.UpgradeInfo> upgrades = armor.getUpgrades(player);
}

ISpiritusHandler will = api.getSpiritusHandler();
double rawSpiritus = will.getCurrentWill(level, pos, SpiritusType.RAW);
```

---

## Anima System

The Anima stores Essentia Vitae (EV) that powers Neo Vitae items, sigils, and rituals. Each player has their own Anima keyed by their `UUID`.

### IAnima

**Package** `com.breakinblocks.neovitae.api.soul`

Represents a player's Anima.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPlayerId()` | `UUID` | Owner UUID |
| `getCurrentEV()` | `int` | Current stored EV |
| `add(AnimaTicket ticket, int maximum)` | `int` | Adds EV from ticket up to `maximum`; returns amount added |
| `set(AnimaTicket ticket, int maximum)` | `int` | Sets EV to the ticket amount (clamped to `maximum`); returns the value set |
| `syphon(AnimaTicket ticket)` | `int` | Drains EV from the anima; returns amount drained |
| `hurtPlayer(Player user, float syphon)` | `void` | Damages the player to compensate for an EV shortfall |
| `syphonAndDamage(Player user, AnimaTicket ticket)` | `SyphonResult` | Drains EV and damages the player if there isn't enough |

#### Example

```java
INeoVitaeAPI api = NeoVitaeAPI.getInstance();
IAnima anima = api.getAnima(player.getUUID());
if (anima == null) return;

AnimaTicket ticket = AnimaTicket.create(100);
SyphonResult result = anima.syphonAndDamage(player, ticket);
if (result.success()) {
    performSigilEffect();
}

AnimaTicket gain = AnimaTicket.create(500);
int added = anima.add(gain, 10000);
```

### AnimaTicket

**Package** `com.breakinblocks.neovitae.api.soul`

An EV transaction descriptor. Currently it tracks only the amount; the type is retained for future logging or auditing hooks.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `create(int amount)` (static) | `AnimaTicket` | Creates a ticket for the given EV amount |
| `getAmount()` | `int` | Returns the EV amount stored on the ticket |

### SyphonResult

**Package** `com.breakinblocks.neovitae.api.soul`

```java
public record SyphonResult(boolean success, int amount) {
    public static SyphonResult of(boolean success, int amount);
    public static SyphonResult failure();
}
```

`success` is `true` when the anima had enough EV to cover the entire request; `amount` is the actual EV syphoned.

---

## Spiritus System

Spiritus is a per-chunk aura that comes in five aspects, stored on chunk data attachments and surfaced through two handlers; one for chunk-level operations and one for items in a player's inventory.

The five aspects are exposed by `com.breakinblocks.neovitae.common.datacomponent.SpiritusType`.

| `SpiritusType` | Display name |
|----------------|--------------|
| `RAW` | Raw |
| `RUINA` | Spiritus Ruina |
| `NIHILUM` | Spiritus Nihilum |
| `VINDICTA` | Spiritus Vindicta |
| `INVICTUS` | Spiritus Invictus |

### ISpiritusHandler

**Package** `com.breakinblocks.neovitae.api.will`

Chunk-level aura handler. All mutating operations are server-side only and silently no-op on the client.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getCurrentWill(Level, BlockPos, SpiritusType)` | `double` | Current aspect amount in the chunk at `pos` |
| `getTotalSpiritus(Level, BlockPos)` | `double` | Sum of all aspects in the chunk |
| `getMaxSpiritus(Level, BlockPos, SpiritusType)` | `double` | Effective maximum including chunk bonus |
| `getBaseMaxSpiritus(SpiritusType)` | `double` | Base maximum from server config |
| `getMaxBonus(Level, BlockPos, SpiritusType)` | `double` | Per-chunk bonus capacity |
| `setMaxBonus(Level, BlockPos, SpiritusType, double)` | `void` | Sets the per-chunk bonus |
| `addMaxBonus(Level, BlockPos, SpiritusType, double)` | `double` | Adjusts the per-chunk bonus; returns new value |
| `addSpiritus(Level, BlockPos, SpiritusType, double)` | `double` | Adds aspect to the chunk; returns amount added |
| `drainSpiritus(Level, BlockPos, SpiritusType, double)` | `double` | Drains aspect from the chunk; returns amount drained |
| `fillWillToAmount(Level, BlockPos, SpiritusType, double)` | `double` | Fills aspect up to a target amount |
| `getDominantWillType(Level, BlockPos)` | `SpiritusType` | Aspect with the highest amount in the chunk |
| `hasSpiritus(Level, BlockPos)` | `boolean` | Whether the chunk has any aspect at all |
| `getFillRatio(Level, BlockPos, SpiritusType)` | `double` | `current / max` in `[0,1]` |
| `queryWill(Level, BlockPos, double threshold)` | `SpiritusState` | Snapshot of all aspects with batch usage tracking |
| `transferWill(Level, ChunkPos from, ChunkPos to, SpiritusType, double maxTransfer)` | `double` | Moves aspect between chunks |

### SpiritusHandler

**Package** `com.breakinblocks.neovitae.api.will`

The default `ISpiritusHandler` implementation. Exposed as `SpiritusHandler.INSTANCE`; addons normally go through `INeoVitaeAPI.getSpiritusHandler()` instead.

### SpiritusState

**Package** `com.breakinblocks.neovitae.api.will`

Immutable per-aspect snapshot of a chunk, with batched draining. Produced by `ISpiritusHandler.queryWill`.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `get(SpiritusType)` | `double` | Snapshot amount for the aspect |
| `has(SpiritusType)` | `boolean` | Whether the amount is at or above the query threshold |
| `getDefault()` / `getCorrosive()` / `getDestructive()` / `getSteadfast()` / `getVengeful()` | `double` | Convenience aliases mapping to `RAW` / `RUINA` / `NIHILUM` / `INVICTUS` / `VINDICTA` |
| `hasDefault()` / `hasCorrosive()` / `hasDestructive()` / `hasSteadfast()` / `hasVengeful()` | `boolean` | Threshold checks for the matching aliases |
| `use(SpiritusType, double amount)` | `void` | Records usage to be drained later |
| `drain(ISpiritusHandler, Level, BlockPos)` | `void` | Applies all accumulated usage |
| `drain(Level, BlockPos)` | `void` | Same as above using the default `SpiritusHandler.INSTANCE` |

#### Example

```java
ISpiritusHandler will = NeoVitaeAPI.getInstance().getSpiritusHandler();
SpiritusState state = will.queryWill(level, pos, 0.5);
if (state.hasDefault()) {
    double scaling = state.getDefault() / 100.0;
    state.use(SpiritusType.RAW, 0.1);
}
state.drain(level, pos);
```

### IPlayerSpiritusHandler

**Package** `com.breakinblocks.neovitae.api.will`

Inventory-level handler for spiritus items and will gems on a player.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getTotalSpiritus(SpiritusType, Player)` | `double` | Sum of an aspect across the player's inventory |
| `getLargestSpiritusType(Player)` | `SpiritusType` | Aspect with the highest total |
| `isSpiritusFull(SpiritusType, Player)` | `boolean` | Whether every gem of that aspect is full |
| `consumeSpiritus(SpiritusType, Player, double)` | `double` | Drains aspect from inventory items; returns amount consumed |
| `addSpiritus(Player, ItemStack)` | `ItemStack` | Pours a will item stack into the player's gems; returns leftover stack |
| `addSpiritus(SpiritusType, Player, double)` | `double` | Adds an aspect amount to inventory gems; returns amount added |
| `addSpiritus(SpiritusType, Player, double, ItemStack ignored)` | `double` | Same as above but skips a specific stack (e.g. the source item) |

---

## Ara Vitae System

The Ara Vitae is Neo Vitae's blood altar; the core crafting block, blood reservoir, and EV source for the soul network.

### Accessing the Altar

Use the block capability provided by Neo Vitae.

```java
import com.breakinblocks.neovitae.api.capability.NVCapabilities;
import com.breakinblocks.neovitae.api.altar.IAraVitae;

IAraVitae altar = level.getCapability(NVCapabilities.ARA_VITAE, pos, null);
if (altar != null) {
    int blood = altar.getCurrentBlood();
    int capacity = altar.getCapacity();
    int tier = altar.getTier();
}
```

See [Capabilities](#capabilities) for details.

### IAraVitae

**Package** `com.breakinblocks.neovitae.api.altar`

Read-mostly view of an Ara Vitae block entity.

#### State

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getTier()` | `int` | Current altar tier (0 = no structure, 1-5 = built structures) |
| `getCurrentBlood()` | `int` | Current EV in the main tank (mB) |
| `getCapacity()` | `int` | Maximum EV capacity (mB), after capacity runes |

#### Crafting

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getProgressFloat()` | `float` | Crafting progress 0.0 - 1.0 |
| `getCraftingProgress()` | `int` | EV consumed so far for the active recipe |
| `getLiquidRequired()` | `int` | Total EV required for the active recipe |
| `getTotalCraftingTime()` | `int` | Total craft duration in ticks |
| `getStackInSlot()` | `ItemStack` | Item in the altar's input slot |

#### Rates

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getConsumptionRate()` | `int` | EV consumed per tick while crafting |
| `getDrainRate()` | `int` | EV lost per tick when crafting is paused |
| `getChargingRate()` | `int` | EV charged per operation when idle |
| `getChargingFrequency()` | `int` | Ticks between idle charge operations |
| `getTickRate()` | `int` | Ticks between altar operations (lower is faster) |

#### Rune-derived bonuses

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getBonusCapacity()` | `float` | Capacity multiplier from runes (1.0 = base) |
| `getEfficiency()` | `float` | Drain multiplier; lower means less blood lost |
| `getSelfSacrificeBonus()` | `float` | Additive self-sacrifice bonus |
| `getSacrificeBonus()` | `float` | Additive entity-sacrifice bonus |
| `getSpeedBonus()` | `float` | Additive crafting-speed bonus |
| `getDislocationBonus()` | `float` | Fluid I/O multiplier |
| `getOrbCapacityBonus()` | `float` | Additive orb fill-rate bonus |

#### Utility

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getFluidHandler()` | `IFluidHandler` | Fluid handler for pipe insertion / extraction |
| `checkTier()` | `void` | Forces a structure rescan and tier recalculation |

### AraVitaeRecipe

**Package** `com.breakinblocks.neovitae.api.recipe`

Abstract `Recipe<AraVitaeInput>` base class for altar recipes. Concrete implementations live in the common package; addons subclass this when they need bespoke serialization or matching logic.

#### Constants

| Field | Value |
|-------|-------|
| `RECIPE_TYPE_NAME` | `"ara_vitae_recipe"` |

#### Constructors

```java
AraVitaeRecipe(Ingredient input, ItemStack result,
               int minTier, int totalBlood, int craftSpeed, int drainSpeed)

AraVitaeRecipe(Ingredient input, ItemStack result,
               int minTier, int totalBlood, int craftSpeed, int drainSpeed,
               boolean copyInputComponents)
```

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getInput()` | `Ingredient` | Recipe input ingredient |
| `getResult()` | `ItemStack` | Copy of the base output stack |
| `getMinTier()` | `int` | Minimum altar tier required |
| `getTotalBlood()` | `int` | Total EV required to complete the craft |
| `getCraftSpeed()` | `int` | EV consumed per tick while crafting |
| `getDrainSpeed()` | `int` | Progress lost per tick when out of EV |
| `shouldCopyInputComponents()` | `boolean` | Whether `assemble` copies data components from the input |
| `matches(AraVitaeInput, Level)` | `boolean` | Matches both ingredient and tier |
| `assemble(AraVitaeInput, HolderLookup.Provider)` | `ItemStack` | Returns the output, optionally with input components copied as a patch |
| `getResultItem(HolderLookup.Provider)` | `ItemStack` | Copy of the base result |
| `canCraftInDimensions(int, int)` | `boolean` | Always `true` |
| `getSerializer()` | `RecipeSerializer<?>` | Abstract; provided by subclasses |
| `getType()` | `RecipeType<?>` | Abstract; provided by subclasses |

#### JSON

```json
{
  "type": "neovitae:ara_vitae_recipe",
  "input": {"item": "minecraft:diamond"},
  "output": {"id": "neovitae:weak_blood_orb"},
  "minTier": 1,
  "bloodNeeded": 2000,
  "craftSpeed": 5,
  "drainSpeed": 1,
  "copyInputComponents": false
}
```

| Field | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| `input` | Ingredient | Yes | - | Input item or tag |
| `output` | ItemStack | Yes | - | Output item with optional components |
| `minTier` | int | Yes | - | Minimum altar tier (0-5) |
| `bloodNeeded` | int | Yes | - | Total EV required |
| `craftSpeed` | int | Yes | - | EV per tick consumption |
| `drainSpeed` | int | Yes | - | Progress loss per tick when out of EV |
| `copyInputComponents` | boolean | No | `false` | Copy input data components onto the output as a patch |

### AraVitaeInput

**Package** `com.breakinblocks.neovitae.api.recipe`

Recipe input pairing the item with the current altar tier.

```java
public class AraVitaeInput implements RecipeInput {
    public AraVitaeInput(ItemStack inputStack, int altarTier);
    public ItemStack getItem(int index);   // index 0 = inputStack, else EMPTY
    public int getAltarTier();
    public int size();                     // always 1
}
```

---

## Altar Rune System

Altar runes are blocks scanned during structure validation that grant the Ara Vitae stat bonuses. Built-in rune types live in `EnumAltarRuneType`; custom types implement `IAltarRuneType` and register via the unified registry.

### IAltarRuneType

**Package** `com.breakinblocks.neovitae.api.altar.rune`

Marker interface (extends `StringRepresentable`) for rune types.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getId()` | `ResourceLocation` | Unique rune identifier |
| `getSerializedName()` | `String` | Lowercase identifier for NBT and JSON |

### EnumAltarRuneType

**Package** `com.breakinblocks.neovitae.api.altar.rune`

Built-in Ara Vitae rune types. Each constant is auto-registered with the rune registry.

| Value | Behavior |
|-------|----------|
| `SPEED` | Increases crafting speed |
| `EFFICIENCY` | Reduces EV drain when crafting is paused |
| `SACRIFICE` | Increases entity-sacrifice EV |
| `SELF_SACRIFICE` | Increases self-sacrifice EV |
| `DISPLACEMENT` | Multiplies fluid I/O rate |
| `CAPACITY` | Increases blood capacity |
| `AUGMENTED_CAPACITY` | Multiplies capacity bonus (compounds) |
| `ORB` | Increases soul network fill rate |
| `ACCELERATION` | Reduces tick rate for altar operations |
| `CHARGING` | Enables and increases stored charge for burst crafting |

| Static method | Description |
|---------------|-------------|
| `fromSerializedName(String)` | Lookup by serialized name; returns `null` if unknown |

### IAltarRuneRegistry

**Package** `com.breakinblocks.neovitae.api.altar.rune`

Unified registry for rune types and block associations. Built-in types are pre-registered; addon mods register their own here.

#### Rune type registration

| Method | Return Type | Description |
|--------|-------------|-------------|
| `registerRuneType(IAltarRuneType)` | `void` | Registers a custom rune type |
| `getRuneType(ResourceLocation)` | `@Nullable IAltarRuneType` | Lookup by ID |
| `getRuneTypeByName(String)` | `@Nullable IAltarRuneType` | Lookup by serialized name |
| `getAllRuneTypes()` | `Collection<IAltarRuneType>` | All registered types (built-in plus custom) |
| `isRegistered(ResourceLocation)` | `boolean` | Whether the ID is registered |

#### Block registration

| Method | Return Type | Description |
|--------|-------------|-------------|
| `registerRuneBlock(Block, IAltarRuneType, int amount)` | `void` | Associates a block with a rune type and amount per block |
| `getRunesForBlock(Block)` | `Map<IAltarRuneType, Integer>` | All rune contributions for a block (empty if none) |
| `hasRunes(Block)` | `boolean` | Whether the block contributes any runes |

#### Example

```java
public class ManaRuneType implements IAltarRuneType {
    public static final ManaRuneType INSTANCE = new ManaRuneType();
    private static final ResourceLocation ID =
        ResourceLocation.fromNamespaceAndPath("mymod", "mana_rune");

    @Override public ResourceLocation getId() { return ID; }
    @Override public String getSerializedName() { return "mana_rune"; }
}

@SubscribeEvent
public static void onCommonSetup(FMLCommonSetupEvent event) {
    event.enqueueWork(() -> {
        IAltarRuneRegistry registry = NeoVitaeAPI.getInstance().getRuneRegistry();
        registry.registerRuneType(ManaRuneType.INSTANCE);
        registry.registerRuneBlock(ModBlocks.MANA_RUNE.get(), ManaRuneType.INSTANCE, 1);
    });
}
```

### AltarRuneModifiers

**Package** `com.breakinblocks.neovitae.api.altar.rune`

Mutable container of altar modifier values passed through the rune events.

#### Getters

| Method | Return Type |
|--------|-------------|
| `getCapacityMod()` | `float` |
| `getTickRate()` | `int` |
| `getConsumptionMod()` | `float` |
| `getSacrificeMod()` | `float` |
| `getSelfSacrificeMod()` | `float` |
| `getDislocationMod()` | `float` |
| `getOrbCapacityMod()` | `float` |
| `getChargeAmountMod()` | `float` |
| `getChargeCapacityMod()` | `float` |
| `getEfficiencyMod()` | `float` |

#### Mutators

| Method | Description |
|--------|-------------|
| `addCapacityMod(float)` | Additive change to capacity |
| `multiplyCapacityMod(float)` | Multiplicative change to capacity |
| `adjustTickRate(int)` | Adjusts tick rate (negative = faster, minimum 1) |
| `setTickRate(int)` | Sets tick rate directly (minimum 1) |
| `addConsumptionMod(float)` | Additive change to crafting speed |
| `addSacrificeMod(float)` | Additive change to sacrifice bonus |
| `addSelfSacrificeMod(float)` | Additive change to self-sacrifice bonus |
| `multiplyDislocationMod(float)` | Multiplies dislocation multiplier |
| `addDislocationMod(float)` | Additive change to dislocation |
| `addOrbCapacityMod(float)` | Additive change to orb capacity |
| `addChargeAmountMod(float)` | Additive change to charge amount |
| `addChargeCapacityMod(float)` | Additive change to charge capacity |
| `multiplyEfficiencyMod(float)` | Multiplies efficiency multiplier |
| `reset()` | Resets all modifiers to defaults |

Mutators are `void`; they do not return `this` and are not chainable.

### RuneInstance

**Package** `com.breakinblocks.neovitae.api.altar.rune`

```java
public record RuneInstance(BlockPos pos, Block block, @Nullable BlockEntity blockEntity) {
    public boolean hasBlockEntity();
    public boolean isBlockEntityType(Class<? extends BlockEntity> type);
    public <T extends BlockEntity> @Nullable T getBlockEntityAs(Class<T> type);
    public boolean isBlockType(Class<? extends Block> blockClass);
}
```

Surfaced on the rune events so addons can react to dynamic rune state without rescanning the altar structure.

---

## Ritual System

Neo Vitae has two flavors of rituals.

- **Rituals** Multi-rune structures driven by a Master Ritual Stone that perform ongoing effects.
- **Imperfect Rituals** Single-use effects triggered by placing a block above an Imperfect Ritual Stone.

### IRitual

**Package** `com.breakinblocks.neovitae.api.ritual`

Interface for multiblock rituals. Custom rituals extend the abstract `Ritual` class in the main package rather than implementing this directly.

#### Core

| Method | Return Type | Description |
|--------|-------------|-------------|
| `performRitual(IMasterRitualStone)` | `void` | Run each refresh while active |
| `getRefreshCost()` | `int` | EV drained per refresh |
| `getRefreshTime()` | `int` | Ticks between refreshes |
| `gatherComponents(Consumer<RitualComponent>)` | `void` | Defines the rune structure |
| `getNewCopy()` | `IRitual` | Creates a fresh instance for a new MRS |

#### Lifecycle

| Method | Return Type | Description |
|--------|-------------|-------------|
| `activateRitual(IMasterRitualStone, Player, UUID owner)` | `boolean` | Called when activation is attempted |
| `stopRitual(IMasterRitualStone, BreakType)` | `void` | Called when the ritual ends |

#### Info

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getName()` | `String` | Unique ritual name |
| `getCrystalLevel()` | `int` | Required activation crystal tier |
| `getActivationCost()` | `int` | EV to activate |
| `getTranslationKey()` | `String` | Lang key |
| `provideInformationOfRitualToPlayer(Player)` | `Component[]` | Lines shown to a player querying the ritual |

#### Ranges

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getBlockRange(String key)` | `AreaDescriptor` | Lookup a named range |
| `getListOfRanges()` | `List<String>` | All modifiable range keys |
| `getModifiableRanges()` | `Map<String, AreaDescriptor>` | All ranges by key |

#### NBT

| Method | Return Type | Description |
|--------|-------------|-------------|
| `readFromNBT(CompoundTag)` | `void` | Loads ritual state |
| `writeToNBT(CompoundTag)` | `void` | Saves ritual state |

#### BreakType

| Value | Description |
|-------|-------------|
| `DEACTIVATE` | Player deactivated |
| `BREAK_MRS` | Master ritual stone broken |
| `BREAK_STONE` | A ritual rune stone was broken |
| `ACTIVATE` | Another ritual activated |
| `REDSTONE` | Redstone signal cut the ritual |
| `EXPLOSION` | Destroyed by explosion |

### IImperfectRitual

**Package** `com.breakinblocks.neovitae.api.ritual`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `onActivate(IImperfectRitualStone, Player)` | `boolean` | Performs the ritual |
| `getName()` | `String` | Unique name |
| `getBlockRequirement()` | `Predicate<BlockState>` | Required block above the stone |
| `getActivationCost()` | `int` | EV cost (may be overridden by data maps) |
| `isLightShow()` | `boolean` | Whether to play lightning effects |
| `getTranslationKey()` | `String` | Lang key |

### IImperfectRitualStone

**Package** `com.breakinblocks.neovitae.api.ritual`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getRitualWorld()` | `Level` | World the stone lives in |
| `getRitualPos()` | `BlockPos` | Position of the stone |

### Master Ritual Stone

`IMasterRitualStone` lives outside the API package, at `com.breakinblocks.neovitae.ritual.IMasterRitualStone`, because its interface returns concrete `Ritual` references rather than the `IRitual` API view. Addons working with rituals will receive `IMasterRitualStone` instances via the API methods on `IRitual`, but should not redeclare or re-implement this interface.

### RitualComponent

**Package** `com.breakinblocks.neovitae.api.ritual`

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

**Package** `com.breakinblocks.neovitae.api.ritual`

| Value | Color | Description |
|-------|-------|-------------|
| `BLANK` | Gray | Basic rune, no elemental affinity |
| `WATER` | Aqua | Water elemental |
| `FIRE` | Red | Fire elemental |
| `EARTH` | Green | Earth elemental |
| `AIR` | White | Air elemental |
| `DUSK` | Dark Gray | Dusk rune, advanced rituals |
| `DAWN` | Gold | Dawn rune, the most powerful rituals |

Public fields `colorCode` (`ChatFormatting`), `translationKey`, and `bookColor` are exposed on each constant. The static helper `byMetadata(int)` looks up a rune by ordinal (returns `BLANK` if out of range).

### AreaDescriptor

**Package** `com.breakinblocks.neovitae.api.ritual`

Abstract base class for ritual areas of effect; ships with three concrete implementations.

#### Abstract methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `resetCache()` | `void` | Clears cached positions |
| `isWithinArea(BlockPos)` | `boolean` | Tests a position |
| `getContainedPositions(BlockPos masterPos)` | `List<BlockPos>` | All positions inside the area |
| `getAABB(BlockPos masterPos)` | `AABB` | Bounding box |
| `modifyAreaByBlockPositions(BlockPos, BlockPos)` | `void` | Resizes the area |
| `isWithinRange(BlockPos, BlockPos, int verticalLimit, int horizontalLimit)` | `boolean` | Range guard |
| `saveToNBT(CompoundTag)` | `void` | Save |
| `loadFromNBT(CompoundTag)` | `void` | Load |
| `copy()` | `AreaDescriptor` | Deep copy |
| `intersects(AreaDescriptor)` | `boolean` | Intersection test |
| `offset(BlockPos)` | `AreaDescriptor` | Returns an offset copy |

Legacy aliases `writeToNBT(CompoundTag)` and `readFromNBT(CompoundTag)` delegate to `saveToNBT` / `loadFromNBT`.

#### Rectangle

```java
new AreaDescriptor.Rectangle(BlockPos min, BlockPos max);
new AreaDescriptor.Rectangle(BlockPos offset, int sizeX, int sizeY, int sizeZ);
AreaDescriptor.Rectangle.createCenteredAt(BlockPos center, int radius, int height);
```

Codec available as `AreaDescriptor.Rectangle.CODEC`. Accessors `getMinimumOffset()` and `getMaximumOffset()`.

#### HemiSphere

```java
new AreaDescriptor.HemiSphere(BlockPos centerOffset, int radius);
```

Accessors `getCenterOffset()` and `getRadius()`.

#### Cross

```java
new AreaDescriptor.Cross(BlockPos centerOffset, int length, int height);
```

Accessors `getCenterOffset()`, `getLength()`, `getHeight()`.

---

## Sigil System

Sigils are items powered by EV. The sigil system is split into two layers.

- **Sigil Effects** (`ISigilEffect`) implement the behavior. Each concrete effect has a `MapCodec` for serialization.
- **Sigil Types** (`SigilType`) are datapack-defined; they pair an effect with EV costs and drain intervals.

### ISigilEffect

**Package** `com.breakinblocks.neovitae.api.sigil`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `codec()` | `MapCodec<? extends ISigilEffect>` | Codec for serialization dispatch |
| `useOnAir(Level, Player, ItemStack)` | `boolean` (default `false`) | Right-click in air; return `true` to charge EV |
| `useOnBlock(Level, Player, ItemStack, BlockPos, Direction, Vec3)` | `boolean` (default `false`) | Right-click on a block |
| `useOnEntity(Level, Player, ItemStack, Entity)` | `boolean` (default `false`) | Right-click on an entity |
| `activeTick(Level, Player, ItemStack, int slot, boolean isSelected)` | `void` (default no-op) | Every tick while a toggleable sigil is on |
| `isToggleable()` | `boolean` (default `false`) | Whether this effect supports toggling |
| `onPlayerLogout(UUID, MinecraftServer)` | `void` (default no-op) | Per-effect logout cleanup hook |

The static field `ISigilEffect.DISPATCH_CODEC` is the dispatch-codec holder; addons read it indirectly via `SigilType.CODEC` and do not need to touch it directly.

### SigilEffect

**Package** `com.breakinblocks.neovitae.api.sigil`

A more specific interface used by NeoVitae's built-in effects. Addons can implement either `SigilEffect` or `ISigilEffect`; the only difference is that `SigilEffect.codec()` is typed as `MapCodec<? extends SigilEffect>`. The static field `SigilEffect.CODEC` is deprecated; prefer `ISigilEffect.DISPATCH_CODEC` via `SigilType`.

### SigilType

**Package** `com.breakinblocks.neovitae.api.sigil`

```java
public record SigilType(
    int lpCostAir,
    int lpCostBlock,
    int lpCostEntity,
    int lpCostActive,
    int drainInterval,
    Optional<ISigilEffect> effect
)
```

| Static | Description |
|--------|-------------|
| `DEFAULT_DRAIN_INTERVAL` | `100` ticks (5 s) |
| `CODEC` | Datapack codec |
| `CLIENT_CODEC` | Full client-sync codec |
| `HOLDER_CODEC` | Registry-fixed holder codec |
| `HOLDER_STREAM_CODEC` | Network stream codec |
| `descriptionId(ResourceKey<SigilType>)` | Returns the `sigil.<namespace>.<path>` translation key |
| `simple(int lpCost, ISigilEffect)` | Factory for a one-shot air-use sigil |
| `toggleable(int lpCostActive, int drainInterval, ISigilEffect)` | Factory for a toggleable sigil |
| `toggleableWithUse(int lpCostBlock, int lpCostActive, int drainInterval, ISigilEffect)` | Factory for a toggleable sigil with a separate block-use action |

| Instance method | Return Type | Description |
|-----------------|-------------|-------------|
| `isToggleable()` | `boolean` | Delegates to the contained effect |
| `getCostForContext(UseContext)` | `int` | Cost for `AIR`, `BLOCK`, `ENTITY`, or `ACTIVE` |

### SigilEffects

**Package** `com.breakinblocks.neovitae.api.sigil.effects`

Constants pointing to the built-in effect codecs. Addons do not need to interact with this class.

| Effect type | Toggleable | Behavior |
|-------------|------------|----------|
| `neovitae:air` | Yes | Provides creative flight |
| `neovitae:place_fluid` | No | Places water or lava |
| `neovitae:void` | No | Voids fluids in an area |
| `neovitae:fast_miner` | Yes | Grants haste |
| `neovitae:green_grove` | Yes | Accelerates crop growth, bone-meals on use |
| `neovitae:magnetism` | Yes | Pulls items and XP orbs |
| `neovitae:frost` | Yes | Freezes water below the player |
| `neovitae:suppression` | Yes | Suppresses fluids in an area |
| `neovitae:phantom_bridge` | Yes | Creates phantom blocks below the player |
| `neovitae:divination` | No | Shows anima info |
| `neovitae:blood_light` | No | Places light blocks |
| `neovitae:teleposition` | No | Teleports to a bound teleposer |
| `neovitae:necromancy` | No | Necromancy effect |
| `neovitae:bound_treasures` | No | Bound-treasures effect |

### Creating a custom sigil effect

#### 1. Implement `ISigilEffect`

```java
public record MyCustomEffect(int power) implements ISigilEffect {

    public static final MapCodec<MyCustomEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.INT.optionalFieldOf("power", 5).forGetter(MyCustomEffect::power)
        ).apply(instance, MyCustomEffect::new)
    );

    @Override
    public MapCodec<? extends ISigilEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) return false;
        player.heal(power);
        return true;
    }
}
```

#### 2. Register the effect codec

```java
public static final DeferredRegister<MapCodec<? extends ISigilEffect>> SIGIL_EFFECTS =
    DeferredRegister.create(NeoVitaeRegistries.SIGIL_EFFECT_TYPE_KEY, "mymod");

public static final Supplier<MapCodec<MyCustomEffect>> MY_CUSTOM_EFFECT =
    SIGIL_EFFECTS.register("my_custom_effect", () -> MyCustomEffect.CODEC);
```

#### 3. Define a sigil type via datapack

`data/mymod/neovitae/sigil_type/my_custom_sigil.json`

```json
{
  "lp_cost_air": 200,
  "drain_interval": 100,
  "effect": {
    "type": "mymod:my_custom_effect",
    "power": 10
  }
}
```

| Field | Type | Required | Default | Description |
|-------|------|----------|---------|-------------|
| `lp_cost_air` | int | No | 0 | EV cost when used on air |
| `lp_cost_block` | int | No | 0 | EV cost when used on a block |
| `lp_cost_entity` | int | No | 0 | EV cost when used on an entity |
| `lp_cost_active` | int | No | 0 | EV cost per drain interval (toggleable) |
| `drain_interval` | int | No | 100 | Ticks between EV drains for toggleable effects |
| `effect` | object | No | - | The effect implementation with `type` and effect-specific fields |

> Sigil item registration uses the registry key from `SigilTypeRegistry.SIGIL_TYPE_KEY` (package `com.breakinblocks.neovitae.registry`), not from `NeoVitaeRegistries`. Addons currently only need this to point a `SigilItem` constructor at their sigil type.

---

## Sentient Armor System

Sentient Armor (formerly Living Armor) gains experience and levels up upgrades as the wearer performs actions.

### ISentientArmorManager

**Package** `com.breakinblocks.neovitae.api.sentient`

Top-level manager for queries and grants. Obtain via `INeoVitaeAPI.getSentientArmorManager()`.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `hasFullSet(Player)` | `boolean` | Whether the player is wearing a full Sentient Armor set |
| `getChestPiece(Player)` | `ItemStack` | The chest piece, or `ItemStack.EMPTY` |
| `getUpgrades(Player)` | `List<UpgradeInfo>` | All upgrades on the chest piece |
| `getUpgradeLevel(Player, ResourceLocation)` | `int` | Level of a specific upgrade, or `0` |
| `grantUpgradeExperience(Player, ResourceLocation, float)` | `boolean` | Grants XP to an upgrade |
| `getUpgradeExperience(Player, ResourceLocation)` | `float` | Current XP on an upgrade |
| `getUsedUpgradePoints(Player)` | `int` | Points currently in use |
| `getMaxUpgradePoints()` | `int` | Default maximum (typically 100) |
| `getMaxUpgradePoints(Player)` | `int` | Effective max for the player's current armor |
| `getAvailableUpgradePoints(Player)` | `int` | Remaining points |

#### UpgradeInfo

```java
public record UpgradeInfo(ResourceLocation upgradeId, int level, float experience, int pointCost) {}
```

### ISentientArmorUpgrade

**Package** `com.breakinblocks.neovitae.api.sentient`

Interface implemented by individual Sentient Armor upgrades.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getMaxLevel()` | `int` | Maximum upgrade level |
| `getLevelFromExp(float)` | `int` | Level for a given XP amount |
| `getExpForNextLevel(int currentLevel)` | `float` | XP needed for the next level (`0` if at max) |
| `getTotalExpForLevel(int level)` | `float` | Total XP required to reach `level` |
| `getPointCost(int level)` | `int` | Upgrade point cost at the given level |
| `getEffects()` | `DataComponentMap` | The data components applied while the upgrade is active |

### IUpgradeHolder

**Package** `com.breakinblocks.neovitae.api.item`

Interface implemented by Sentient Armor item classes; useful for `instanceof` checks from addons.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getMaxUpgradePoints(ItemStack, Player)` | `int` | Effective max points for this stack/player |
| `hasFullSentientArmorSet(Player)` | `boolean` | Whether the player is wearing a full set |
| `isInvalidArmor(ItemStack)` | `boolean` | Whether the armor is "dead" or otherwise invalid |

---

## Tranquility / Incense

Tranquility powers the Incense Altar's bonus multiplier during self-sacrifice. Each block can map to a `EnumTranquilityType` and a value via the `tranquility` data map.

### ITranquilityHandler

**Package** `com.breakinblocks.neovitae.api.incense`

Obtain via `INeoVitaeAPI.getTranquilityHandler()`.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getTranquilityType(Block)` | `@Nullable EnumTranquilityType` | Aspect type for a block |
| `getTranquilityType(BlockState)` | `@Nullable EnumTranquilityType` | Same, by block state |
| `getTranquilityValue(Block)` | `double` | Numeric tranquility value, or `0` if none |
| `getTranquilityValue(BlockState)` | `double` | Same, by block state |
| `hasTranquility(Block)` | `boolean` | Whether the block has any entry |
| `hasTranquility(BlockState)` | `boolean` | Same, by block state |

`EnumTranquilityType` constants `PLANT`, `CROP`, `TREE`, `EARTHEN`, `WATER`, `FIRE`, `LAVA` live at `com.breakinblocks.neovitae.incense.EnumTranquilityType`.

### TranquilityHandler

**Package** `com.breakinblocks.neovitae.api.incense`

Default singleton implementation exposed as `TranquilityHandler.INSTANCE`; addons normally use the API accessor instead.

### Datapack format

```json
{
  "values": {
    "#minecraft:logs": { "type": "tree", "value": 1.0 },
    "mymod:magic_flower": { "type": "plant", "value": 2.0 }
  }
}
```

When a block matches multiple entries (via tags) the highest value wins.

---

## Routing System

The routing system is the public surface for Neo Vitae's item, fluid, and energy routing network. The network is built from conduit nodes which discover one master node; the master iterates `RoutingChannel` implementations each tick to transfer between input and output sides.

### IRoutingNode

**Package** `com.breakinblocks.neovitae.api.routing`

Base interface for every routing node.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `connectMasterToRemainingNode(Level, List<BlockPos> alreadyChecked, IMasterRoutingNode)` | `void` | Propagates the master through the network |
| `getCurrentBlockPos()` | `BlockPos` | This node's position |
| `getConnected()` | `List<BlockPos>` | Adjacent node positions |
| `getMasterPos()` | `BlockPos` | Position of the node's master |
| `isConnectionEnabled(BlockPos)` | `boolean` | Whether a specific connection is enabled |
| `isMaster(IMasterRoutingNode)` | `boolean` | Whether this node points at the given master |
| `addConnection(BlockPos)` | `void` | Adds a connection |
| `removeConnection(BlockPos)` | `void` | Removes a connection |
| `removeAllConnections()` | `void` | Removes every connection |
| `checkAndPurgeConnectionToMaster(BlockPos ignorePos)` | `List<BlockPos>` | Purges stale master connections |
| `recheckConnectionToMaster(List<BlockPos>, List<IRoutingNode>)` | `Triple<Boolean, List<BlockPos>, List<IRoutingNode>>` | Re-discovers the master |

### IMasterRoutingNode

**Package** `com.breakinblocks.neovitae.api.routing`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `isConnected(List<BlockPos>, BlockPos)` | `boolean` | Whether a path exists to a node |
| `addNodeToList(IRoutingNode)` | `void` | Tracks a node |
| `addConnections(BlockPos, List<BlockPos>)` | `void` | Bulk add connections |
| `addConnection(BlockPos, BlockPos)` | `void` | Adds a bidirectional connection |
| `removeConnection(BlockPos, BlockPos)` | `void` | Removes a bidirectional connection |
| `removeNodeFromGraph(BlockPos)` | `void` | Scrubs a node entirely from the graph |

### Item routing

| Interface | Adds |
|-----------|------|
| `IItemRoutingNode` | `isInventoryConnectedToSide(Direction)`, `getPriority(Direction)` |
| `IInputItemRoutingNode` (extends `IItemRoutingNode`) | `isInput(Direction)`, `getInputFilterForSide(Direction)` |
| `IOutputItemRoutingNode` (extends `IItemRoutingNode`) | `isOutput(Direction)`, `getOutputFilterForSide(Direction)` |

### Fluid routing

| Interface | Adds |
|-----------|------|
| `IFluidRoutingNode` | `isTankConnectedToSide(Direction)`, `getFluidPriority(Direction)` |
| `IInputFluidRoutingNode` | `isFluidInput(Direction)`, `getInputFluidFilterForSide(Direction)` |
| `IOutputFluidRoutingNode` | `isFluidOutput(Direction)`, `getOutputFluidFilterForSide(Direction)` |

### Filters

`IRoutingFilter` is the base marker (also exposes an optional `getNodePos()` for visual hooks).

#### IItemFilter

| Method | Return Type | Description |
|--------|-------------|-------------|
| `initializeFilter(List<IFilterKey>, BlockEntity, IItemHandler, boolean isFilterOutput)` | `void` | Binds a filter to an inventory |
| `initializeFilter(List<IFilterKey>)` | `void` | Binds with no inventory context |
| `transferStackThroughOutputFilter(ItemStack)` | `ItemStack` | Pushes through the output filter |
| `transferThroughInputFilter(IItemFilter, int maxTransfer)` | `int` | Pulls into a paired output filter |
| `doesStackPassFilter(ItemStack)` | `boolean` | Passes-through test |
| `doStacksMatch(IFilterKey, ItemStack)` | `boolean` | Single-key match |
| `getFilterList()` | `List<IFilterKey>` | Underlying filter list (read-only contract) |

#### IFluidFilter

Mirrors `IItemFilter` for fluids; uses `FluidStack` and `IFluidHandler` parameters.

#### IEnergyFilter

| Method | Return Type | Description |
|--------|-------------|-------------|
| `transferEnergyThroughOutputFilter(int)` | `int` | Energy push step |
| `transferThroughInputFilter(IEnergyFilter, int maxTransfer)` | `int` | Energy pull step |

#### IFilterKey

| Method | Return Type | Description |
|--------|-------------|-------------|
| `doesStackMatch(ItemStack)` | `boolean` | Per-key match logic |
| `getCount()` / `setCount(int)` / `grow(int)` / `shrink(int)` | various | Count manipulation |
| `isEmpty()` | `boolean` | Whether the key has any count |

### RoutingChannel

**Package** `com.breakinblocks.neovitae.api.routing`

Each channel handles one resource type. Register new channels during mod construction via `RoutingChannelRegistry.register`.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `id()` | `String` | Persistence/NBT key |
| `isInputNode(BlockEntity)` / `isOutputNode(BlockEntity)` | `boolean` | Node-eligibility checks |
| `isConnectedOnSide(BlockEntity, Direction)` | `boolean` | Capability availability test |
| `isInputSide(...)` / `isOutputSide(...)` | `boolean` | Direction-specific I/O test |
| `getPriority(BlockEntity, Direction)` | `int` | Side priority |
| `getInputFilter(BlockEntity, Direction)` | `@Nullable F` | Resolves the input filter |
| `getOutputFilter(BlockEntity, Direction)` | `@Nullable F` | Resolves the output filter |
| `getMaxTransfer(BlockEntity masterNode)` | `int` | Per-tick transfer cap |
| `transfer(F input, F output, int maxTransfer)` | `int` | Actually performs the transfer |

### RoutingChannelRegistry

**Package** `com.breakinblocks.neovitae.api.routing`

| Method | Description |
|--------|-------------|
| `register(RoutingChannel<?>)` | Registers a new channel |
| `getChannels()` | Returns an unmodifiable list of registered channels |

---

## Stream Effects

`StreamEffect` is a server-driven, client-rendered visual energy stream or stationary blob. Build one with the fluent `Builder`, then call `sendToNearby` to broadcast.

### StreamEffect

**Package** `com.breakinblocks.neovitae.api.stream`

Immutable holder; see source for the full set of public final fields (`color`, `scale`, `speed`, `gravity`, `wobbleAmplitude`, `spiralRadius`, etc.).

#### Static factories

| Method | Description |
|--------|-------------|
| `builder(double x, double y, double z)` | Builder from exact coordinates |
| `builder(Entity)` | Builder sourced from an entity (75% bbHeight) |
| `builder(BlockPos)` | Builder sourced from a block center |
| `decode(FriendlyByteBuf)` | Network deserialization helper |

#### Methods

| Method | Description |
|--------|-------------|
| `sendToNearby(ServerLevel, BlockPos center, double radius)` | Broadcast to players within radius |
| `sendToNearby(ServerLevel, double radius)` | Same, centered on the source |
| `encode(FriendlyByteBuf)` | Network serialization |

#### Builder

`Builder` exposes a fluent setter for every renderable parameter, including `to(double,double,double)` / `to(BlockPos)` / `to(Entity)` / `toTracked(Entity)`, `color`, `scale`, `alphaStart`, `alphaEnd`, `alpha`, `glow`, `tubeSegments`, `speed`, `gravity`, `wobble`, `wobbleFrequency`, `spiralInto`, `spiralRadius`, `spiralSpeed`, `approachHeight`, `lifetime`, `drainSpeed`, `stationary`, `blockyMode`, `trailDensity`, `rawTrailColor`. Call `build()` to finalize.

```java
StreamEffect.builder(player)
    .to(altarPos)
    .color(0xBB0000)
    .speed(1.5f)
    .build()
    .sendToNearby(serverLevel, altarPos, 128);
```

### BlockyMode

**Package** `com.breakinblocks.neovitae.api.stream`

Enum: `NONE`, `BLOCKY`, `BLOCKY_STEPS`, `BLOCKY_UNIFORM`, `BLOCKY_BOX`, `BLOCKY_BEAM`. Controls the stream's optional "blocky" voxel look.

### StreamPresets

**Package** `com.breakinblocks.neovitae.api.stream`

Pre-configured `Builder` recipes. Each preset returns a fully tuned `StreamEffect.Builder` that callers may further customize before `build()`.

| Preset | Description |
|--------|-------------|
| `bloodTendril(Entity/BlockPos, BlockPos)` | Viscous crimson tendril |
| `soulSiphon(Entity/BlockPos, BlockPos)` | Thin ghostly blue-white wisp |
| `voidTendril(Entity/BlockPos, BlockPos)` | Thick near-black mass |
| `lifePulse(Entity/BlockPos, BlockPos)` | Warm golden surge |
| `demonTether(Entity/BlockPos, BlockPos)` | Fiery orange chain |
| `corruptionSeep(Entity/BlockPos, BlockPos)` | Sickly green ooze |
| `arcaneBolt(Entity/BlockPos, BlockPos)` | Saturated purple thread |
| `blockBolt` / `blockBolt2` … `blockBolt6` | Voxel-styled bolts with different `BlockyMode` settings |
| `emberMote(BlockPos)` | Stationary floating ember |
| `soulWisp(BlockPos)` | Stationary pale wisp |
| `voidMark(BlockPos)` | Stationary heavy void stain |

---

## Custom Player Attributes

Neo Vitae registers custom player attributes that addon mods can apply modifiers to via equipment, effects, or data packs.

**Package** `com.breakinblocks.neovitae.common.attribute.NVAttributes`

### Attribute Reference

| Holder Field | Registry ID | Default | Max | Description |
|-------------|------------|---------|-----|-------------|
| `SELF_SACRIFICE_MULTIPLIER` | `neovitae:player.self_sacrifice_multiplier` | 1.0 | 100.0 | Multiplier for EV from self-sacrifice (PercentageAttribute) |
| `BONUS_SACRIFICE` | `neovitae:bonus_sacrifice` | 0.0 | 1000.0 | % bonus EV from Lamina Exhauriens mob kills |
| `BONUS_SELF_SACRIFICE` | `neovitae:bonus_self_sacrifice` | 0.0 | 1000.0 | % bonus EV from Lamina Maleficus self-sacrifice |
| `BONUS_SPIRITUS` | `neovitae:bonus_spiritus` | 0.0 | 1000.0 | % bonus Spiritus drops |
| `SIGIL_COST_REDUCTION` | `neovitae:sigil_cost_reduction` | 0.0 | 100.0 | % reduction to sigil EV costs |
| `BLOOD_SIPHON` | `neovitae:blood_siphon` | 0.0 | 1024.0 | Converts damage dealt into EV |
| `BLOOD_SHIELD` | `neovitae:blood_shield` | 0.0 | 10.0 | Reduces incoming damage, drains EV |

All attributes are registered to the Player entity type and are syncable to clients.

### Using Attributes from Addon Mods

```java
import com.breakinblocks.neovitae.common.attribute.NVAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

double siphon = player.getAttributeValue(NVAttributes.BLOOD_SIPHON);

player.getAttribute(NVAttributes.BONUS_SACRIFICE).addTransientModifier(
    new AttributeModifier(
        ResourceLocation.fromNamespaceAndPath("mymod", "sacrifice_bonus"),
        25.0,
        AttributeModifier.Operation.ADD_VALUE
    )
);
```

### Blood Siphon Details

EV gained = min(attribute_value, damage_dealt) × multiplier.
- vs Players, multiplier defaults to 100 and drains from the target's anima.
- vs Mobs, multiplier defaults to 10 and EV is generated from nothing.

### Blood Shield Details

Damage reduction = 10% per attribute point (hard cap 99%). EV cost = damage_prevented × configurable multiplier (default 100). If there isn't enough EV the shield partially absorbs and the remainder passes through.

### Server Configuration

Multipliers are configurable in `config/neovitae-server.toml` under `[blood_attributes]`.
- `siphon_player_multiplier` (default 100)
- `siphon_mob_multiplier` (default 10)
- `shield_lp_cost_multiplier` (default 100)

---

## Events

Neo Vitae fires NeoForge events on the game event bus.

### AnimaEvent

**Package** `com.breakinblocks.neovitae.api.event`

Base event for EV transactions. Subtypes share these getters.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getNetwork()` | `IAnima` | The anima involved |
| `getOwnerId()` | `UUID` | Owner UUID |
| `getTicket()` | `AnimaTicket` | Ticket describing the transaction |
| `getAmount()` | `int` | Ticket amount |

#### AnimaEvent.PreSyphon (cancellable)

Fires before EV is drained.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getModifiedAmount()` | `int` | Current modified amount |
| `setModifiedAmount(int)` | `void` | Clamps the syphon amount (cannot exceed original) |

#### AnimaEvent.PostSyphon

Fires after EV is drained.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getActualAmount()` | `int` | Amount actually removed |

#### AnimaEvent.PreAdd (cancellable)

Fires before EV is added.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getMaximum()` | `int` | Anima cap |
| `getModifiedAmount()` | `int` | Current modified amount |
| `setModifiedAmount(int)` | `void` | Sets the amount to add |

#### AnimaEvent.PostAdd

Fires after EV is added.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getMaximum()` | `int` | Anima cap |
| `getActualAmount()` | `int` | Amount actually added |

```java
@SubscribeEvent
public void onPreSyphon(AnimaEvent.PreSyphon event) {
    event.setModifiedAmount((int) (event.getAmount() * 0.9));
}
```

### AltarRuneEvent

**Package** `com.breakinblocks.neovitae.api.event`

Fired during Ara Vitae structure scanning and stat calculation.

#### Base getters

| Method | Return Type |
|--------|-------------|
| `getAltar()` | `IAraVitae` |
| `getLevel()` | `Level` |
| `getPos()` | `BlockPos` |
| `getTier()` | `int` |

#### AltarRuneEvent.GatherRunes

Fired after the structure has been scanned for rune blocks. Addons add virtual rune counts here.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getRuneCounts()` | `Map<IAltarRuneType, Integer>` | Mutable map (rune count) |
| `addRunes(IAltarRuneType, int)` | `void` | Convenience accumulator |
| `getRuneInstances()` | `List<RuneInstance>` | Unmodifiable scanned instances |

#### AltarRuneEvent.CalculateStats

Fired during stat calculation. Addons mutate `AltarRuneModifiers` here.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getModifiers()` | `AltarRuneModifiers` | Mutable modifiers |
| `getRuneCounts()` | `Map<IAltarRuneType, Integer>` | Unmodifiable final counts |
| `getRuneCount(IAltarRuneType)` | `int` | Count for a single type |
| `getRuneInstances()` | `List<RuneInstance>` | All scanned instances |
| `getRuneBlockEntities(Class<T>)` | `List<T>` | Filtered to a specific block entity type |
| `getRuneInstancesByType(IAltarRuneType)` | `List<RuneInstance>` | Filtered by rune type |

#### AltarRuneEvent.PostCalculate

Informational; modifiers are finalized.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getFinalModifiers()` | `AltarRuneModifiers` | Final modifier values |
| `getRuneInstances()` | `List<RuneInstance>` | All scanned instances |

### SentientArmorEvent

**Package** `com.breakinblocks.neovitae.api.event`

Base getters expose `getWearer()` and `getArmorPiece()`.

#### SentientArmorEvent.ExperienceGain (cancellable)

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getUpgradeId()` | `ResourceLocation` | Upgrade about to gain XP |
| `getExperience()` | `float` | Pending XP amount |
| `setExperience(float)` | `void` | Adjust the XP amount |

#### SentientArmorEvent.LevelUp

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getUpgradeId()` | `ResourceLocation` | Upgrade that leveled up |
| `getPreviousLevel()` | `int` | Level before the level-up |
| `getNewLevel()` | `int` | Level after the level-up |

```java
@SubscribeEvent
public void onLevelUp(SentientArmorEvent.LevelUp event) {
    Player player = event.getWearer();
    player.sendSystemMessage(Component.literal(
        event.getUpgradeId() + " leveled to " + event.getNewLevel()));
}
```

---

## Capabilities

### NVCapabilities

**Package** `com.breakinblocks.neovitae.api.capability`

| Field | Type | Description |
|-------|------|-------------|
| `ARA_VITAE` | `BlockCapability<IAraVitae, @Nullable Direction>` | Read-mostly access to an Ara Vitae block entity |

Capabilities are registered automatically by Neo Vitae at `RegisterCapabilitiesEvent` time; addons just query them.

```java
IAraVitae altar = level.getCapability(NVCapabilities.ARA_VITAE, pos, null);
```

---

## Registry Keys

### NeoVitaeRegistries

**Package** `com.breakinblocks.neovitae.api.registry`

| Key | Type | Description |
|-----|------|-------------|
| `RITUAL_KEY` | `ResourceKey<Registry<IRitual>>` | Regular (multi-rune) rituals |
| `IMPERFECT_RITUAL_KEY` | `ResourceKey<Registry<IImperfectRitual>>` | Imperfect rituals |
| `SIGIL_EFFECT_TYPE_KEY` | `ResourceKey<Registry<MapCodec<? extends ISigilEffect>>>` | Sigil effect type registry |

> The sigil **type** datapack registry key (`SigilType` entries themselves) is `SigilTypeRegistry.SIGIL_TYPE_KEY` in `com.breakinblocks.neovitae.registry`, not in this API class.

### Registering custom content

```java
public static final DeferredRegister<Ritual> RITUALS =
    DeferredRegister.create(NeoVitaeRegistries.RITUAL_KEY, "yourmodid");

public static final DeferredHolder<Ritual, MyCustomRitual> MY_RITUAL =
    RITUALS.register("my_ritual", MyCustomRitual::new);

public YourMod(IEventBus modBus) {
    RITUALS.register(modBus);
}
```

```java
public static final DeferredRegister<ImperfectRitual> IMPERFECT_RITUALS =
    DeferredRegister.create(NeoVitaeRegistries.IMPERFECT_RITUAL_KEY, "yourmodid");

public static final DeferredHolder<ImperfectRitual, MyImperfectRitual> MY_RITUAL =
    IMPERFECT_RITUALS.register("my_ritual", MyImperfectRitual::new);
```

---

## API Package Structure

All API classes live in the main source set at `src/main/java/com/breakinblocks/neovitae/api/`. There is no separate API artifact or source set.

```
com.breakinblocks.neovitae.api/
├── NeoVitaeAPI.java
├── INeoVitaeAPI.java
├── altar/
│   ├── IAraVitae.java
│   └── rune/
│       ├── AltarRuneModifiers.java
│       ├── EnumAltarRuneType.java
│       ├── IAltarRuneRegistry.java
│       ├── IAltarRuneType.java
│       └── RuneInstance.java
├── capability/
│   └── NVCapabilities.java
├── event/
│   ├── AltarRuneEvent.java
│   ├── AnimaEvent.java
│   └── SentientArmorEvent.java
├── incense/
│   ├── ITranquilityHandler.java
│   └── TranquilityHandler.java
├── item/
│   └── IUpgradeHolder.java
├── recipe/
│   ├── AraVitaeInput.java
│   └── AraVitaeRecipe.java
├── registry/
│   └── NeoVitaeRegistries.java
├── ritual/
│   ├── AreaDescriptor.java        # + Rectangle, HemiSphere, Cross
│   ├── EnumRuneType.java
│   ├── IImperfectRitual.java
│   ├── IImperfectRitualStone.java
│   ├── IRitual.java
│   └── RitualComponent.java
├── routing/
│   ├── IRoutingNode.java
│   ├── IMasterRoutingNode.java
│   ├── IItemRoutingNode.java
│   ├── IInputItemRoutingNode.java
│   ├── IOutputItemRoutingNode.java
│   ├── IFluidRoutingNode.java
│   ├── IInputFluidRoutingNode.java
│   ├── IOutputFluidRoutingNode.java
│   ├── IItemFilter.java
│   ├── IFluidFilter.java
│   ├── IEnergyFilter.java
│   ├── IRoutingFilter.java
│   ├── IFilterKey.java
│   ├── RoutingChannel.java
│   └── RoutingChannelRegistry.java
├── sentient/
│   ├── ISentientArmorManager.java
│   └── ISentientArmorUpgrade.java
├── sigil/
│   ├── ISigilEffect.java
│   ├── SigilEffect.java
│   ├── SigilType.java
│   └── effects/
│       └── SigilEffects.java       # + built-in effect classes
├── soul/
│   ├── IAnima.java
│   ├── AnimaTicket.java
│   └── SyphonResult.java
├── stream/
│   ├── StreamEffect.java
│   ├── StreamPresets.java
│   └── BlockyMode.java
└── will/
    ├── ISpiritusHandler.java
    ├── IPlayerSpiritusHandler.java
    ├── SpiritusHandler.java
    └── SpiritusState.java
```

### Key Non-API Classes for Addon Use

These are in `com.breakinblocks.neovitae.common` (not the API package) but commonly used by addons.

| Class | Package | Purpose |
|-------|---------|---------|
| `NVAttributes` | `common.attribute` | Custom player attributes (Blood Siphon, Blood Shield, etc.) |
| `NVItems` | `common.item` | Item registry |
| `NVBlocks` | `common.block` | Block registry |
| `NVMobEffects` | `common.effect` | Custom mob effects (Flight, Bounce, Gravity, etc.) |
| `SpiritusType` | `common.datacomponent` | Spiritus aspect enum (RAW, RUINA, NIHILUM, VINDICTA, INVICTUS) |
| `NVDataComponents` | `common.datacomponent` | Data component registry |
| `ForgeRecipe` | `common.recipe.forge` | Hellfire Forge recipe class |
| `FlaskRecipe` | `common.recipe.flask` | Flask recipe base class |
| `SigilTypeRegistry` | `registry` | Holds `SIGIL_TYPE_KEY` for sigil-type registry references |

---

## Important Notes

- **Hellfire Forge** Recipe type is `neovitae:hellfire_forge`, recipe directory is `hellfire_forge/`.
- **API classes in main JAR** there is no separate API artifact or source set.
- **Modonomicon transitive dependency** addons that compile against NeoVitae may need Modonomicon on the classpath (for `NVGuideBookItem`).

When reporting issues, please include the Neo Vitae version, NeoForge version, relevant code snippets, and full error logs.
