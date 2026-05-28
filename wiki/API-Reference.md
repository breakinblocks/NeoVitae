# API Reference

Full per-system reference for the Neo Vitae addon API on NeoForge 1.21.1. For setup, packaging notes, and quick examples see [API Overview](API-Overview). For the player-facing side of these systems see [Rituals](Rituals), [Sigils](Sigils), and [Sentient Armor](Sentient-Armor).

The main entry point is `NeoVitaeAPI.getInstance()`, which returns an `INeoVitaeAPI` once Neo Vitae has finished common setup. Its top-level surface is:

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getAnima(UUID uuid)` | `@Nullable IAnima` | Anima for a player by UUID |
| `getSentientArmorManager()` | `ISentientArmorManager` | Sentient Armor manager |
| `getRuneRegistry()` | `IAltarRuneRegistry` | Altar rune registry |
| `getTranquilityHandler()` | `ITranquilityHandler` | Tranquility lookup |
| `getSpiritusHandler()` | `ISpiritusHandler` | Chunk-based spiritus handler |
| `getPlayerWillHandler()` | `IPlayerSpiritusHandler` | Inventory-level spiritus handler |
| `getApiVersion()` | `String` | API version string |

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
```

---

## Anima System

The Anima stores Essentia Vitae (EV) that powers Neo Vitae items, sigils, and rituals. Each player has their own Anima, keyed by their `UUID`.

### IAnima

**Package** `com.breakinblocks.neovitae.api.soul`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getPlayerId()` | `UUID` | Owner UUID |
| `getCurrentEV()` | `int` | Current stored EV |
| `add(AnimaTicket ticket, int maximum)` | `int` | Adds EV up to `maximum`; returns amount added |
| `set(AnimaTicket ticket, int maximum)` | `int` | Sets EV to the ticket amount (clamped); returns value set |
| `syphon(AnimaTicket ticket)` | `int` | Drains EV; returns amount drained |
| `hurtPlayer(Player user, float syphon)` | `void` | Damages the player to compensate for an EV shortfall |
| `syphonAndDamage(Player user, AnimaTicket ticket)` | `SyphonResult` | Drains EV and damages the player if insufficient |

```java
IAnima anima = NeoVitaeAPI.getInstance().getAnima(player.getUUID());
if (anima == null) return;

SyphonResult result = anima.syphonAndDamage(player, AnimaTicket.create(100));
if (result.success()) {
    performSigilEffect();
}

int added = anima.add(AnimaTicket.create(500), 10000);
```

### AnimaTicket

**Package** `com.breakinblocks.neovitae.api.soul`

Tracks the EV amount for a transaction. The type is retained for future logging hooks.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `create(int amount)` (static) | `AnimaTicket` | Creates a ticket |
| `getAmount()` | `int` | Returns the EV amount |

### SyphonResult

**Package** `com.breakinblocks.neovitae.api.soul`

```java
public record SyphonResult(boolean success, int amount) {
    public static SyphonResult of(boolean success, int amount);
    public static SyphonResult failure();
}
```

---

## Spiritus System

Spiritus is a per-chunk aura with five aspects. The aspect enum `SpiritusType` is at `com.breakinblocks.neovitae.common.datacomponent.SpiritusType`.

| `SpiritusType` | Display name |
|----------------|--------------|
| `RAW` | Raw |
| `RUINA` | Spiritus Ruina |
| `NIHILUM` | Spiritus Nihilum |
| `VINDICTA` | Spiritus Vindicta |
| `INVICTUS` | Spiritus Invictus |

### ISpiritusHandler

**Package** `com.breakinblocks.neovitae.api.spiritus`

All mutating methods are server-side only and no-op on the client.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getCurrentWill(Level, BlockPos, SpiritusType)` | `double` | Current aspect amount |
| `getTotalSpiritus(Level, BlockPos)` | `double` | Sum across aspects |
| `getMaxSpiritus(Level, BlockPos, SpiritusType)` | `double` | Effective maximum |
| `getBaseMaxSpiritus(SpiritusType)` | `double` | Base maximum from config |
| `getMaxBonus(Level, BlockPos, SpiritusType)` | `double` | Per-chunk bonus capacity |
| `setMaxBonus(Level, BlockPos, SpiritusType, double)` | `void` | Sets per-chunk bonus |
| `addMaxBonus(Level, BlockPos, SpiritusType, double)` | `double` | Adjusts per-chunk bonus; returns new value |
| `addSpiritus(Level, BlockPos, SpiritusType, double)` | `double` | Adds aspect to the chunk |
| `drainSpiritus(Level, BlockPos, SpiritusType, double)` | `double` | Drains aspect from the chunk |
| `fillWillToAmount(Level, BlockPos, SpiritusType, double)` | `double` | Fills aspect up to a target |
| `getDominantWillType(Level, BlockPos)` | `SpiritusType` | Aspect with the highest amount |
| `hasSpiritus(Level, BlockPos)` | `boolean` | Whether the chunk has any aspect |
| `getFillRatio(Level, BlockPos, SpiritusType)` | `double` | `current / max` |
| `queryWill(Level, BlockPos, double threshold)` | `SpiritusState` | Snapshot with batched usage tracking |
| `transferWill(Level, ChunkPos, ChunkPos, SpiritusType, double)` | `double` | Moves aspect between chunks |

### SpiritusHandler

**Package** `com.breakinblocks.neovitae.api.spiritus`

Default implementation; available as `SpiritusHandler.INSTANCE`. Addons should use the API accessor.

### SpiritusState

**Package** `com.breakinblocks.neovitae.api.spiritus`

Per-aspect snapshot with batch draining; produced by `ISpiritusHandler.queryWill`.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `get(SpiritusType)` | `double` | Snapshot amount |
| `has(SpiritusType)` | `boolean` | At or above threshold |
| `getRaw()` / `getRuina()` / `getNihilum()` / `getInvictus()` / `getVindicta()` | `double` | Per-aspect amount accessors; convenience over `get(SpiritusType.X)`. |
| `hasRaw()` / `hasRuina()` / `hasNihilum()` / `hasInvictus()` / `hasVindicta()` | `boolean` | Per-aspect threshold checks; convenience over `has(SpiritusType.X)`. |
| `use(SpiritusType, double)` | `void` | Records pending usage |
| `drain(ISpiritusHandler, Level, BlockPos)` | `void` | Applies all pending usage |
| `drain(Level, BlockPos)` | `void` | Same as above using `SpiritusHandler.INSTANCE` |

```java
ISpiritusHandler spiritus = NeoVitaeAPI.getInstance().getSpiritusHandler();
SpiritusState state = spiritus.querySpiritus(level, pos, 0.5);
if (state.hasDefault()) {
    state.use(SpiritusType.RAW, 0.1);
}
state.drain(level, pos);
```

### IPlayerSpiritusHandler

**Package** `com.breakinblocks.neovitae.api.spiritus`

Inventory-level handler for spiritus items and spiritus gems.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getTotalSpiritus(SpiritusType, Player)` | `double` | Sum across inventory |
| `getLargestSpiritusType(Player)` | `SpiritusType` | Aspect with the highest total |
| `isSpiritusFull(SpiritusType, Player)` | `boolean` | Every gem of that aspect is full |
| `consumeSpiritus(SpiritusType, Player, double)` | `double` | Drains aspect from gems |
| `addSpiritus(Player, ItemStack)` | `ItemStack` | Pours a spiritus-item stack into gems; returns leftover |
| `addSpiritus(SpiritusType, Player, double)` | `double` | Adds aspect to gems |
| `addSpiritus(SpiritusType, Player, double, ItemStack ignored)` | `double` | Same but skips a stack |

---

## Ara Vitae System

The Ara Vitae is Neo Vitae's blood altar.

### Accessing the Altar

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

### IAraVitae

**Package** `com.breakinblocks.neovitae.api.altar`

#### State

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getTier()` | `int` | Altar tier (0 = no structure, 1-5 built) |
| `getCurrentBlood()` | `int` | Current EV in the tank (mB) |
| `getCapacity()` | `int` | Maximum EV (mB) with rune bonuses |

#### Crafting

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getProgressFloat()` | `float` | Progress 0.0 - 1.0 |
| `getCraftingProgress()` | `int` | EV consumed so far |
| `getLiquidRequired()` | `int` | Total EV for the active recipe |
| `getTotalCraftingTime()` | `int` | Total craft duration in ticks |
| `getStackInSlot()` | `ItemStack` | Input item |

#### Rates

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getConsumptionRate()` | `int` | EV per tick while crafting |
| `getDrainRate()` | `int` | EV per tick lost when paused |
| `getChargingRate()` | `int` | EV per idle charge |
| `getChargingFrequency()` | `int` | Ticks between idle charges |
| `getTickRate()` | `int` | Ticks between altar operations |

#### Rune-derived bonuses

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getBonusCapacity()` | `float` | Capacity multiplier |
| `getEfficiency()` | `float` | Drain multiplier |
| `getSelfSacrificeBonus()` | `float` | Additive self-sacrifice bonus |
| `getSacrificeBonus()` | `float` | Additive entity-sacrifice bonus |
| `getSpeedBonus()` | `float` | Additive crafting-speed bonus |
| `getDislocationBonus()` | `float` | Fluid I/O multiplier |
| `getOrbCapacityBonus()` | `float` | Additive orb fill-rate bonus |

#### Utility

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getFluidHandler()` | `IFluidHandler` | Fluid handler |
| `checkTier()` | `void` | Forces a structure rescan |

### AraVitaeRecipe

**Package** `com.breakinblocks.neovitae.api.recipe`

Abstract base class for altar recipes (`Recipe<AraVitaeInput>`).

| Static | Value |
|--------|-------|
| `RECIPE_TYPE_NAME` | `"ara_vitae_recipe"` |

Constructors:

```java
AraVitaeRecipe(Ingredient input, ItemStack result,
               int minTier, int totalBlood, int craftSpeed, int drainSpeed);
AraVitaeRecipe(Ingredient input, ItemStack result,
               int minTier, int totalBlood, int craftSpeed, int drainSpeed,
               boolean copyInputComponents);
```

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getInput()` | `Ingredient` | Input ingredient |
| `getResult()` | `ItemStack` | Copy of base output |
| `getMinTier()` | `int` | Minimum altar tier |
| `getTotalBlood()` | `int` | Total EV required |
| `getCraftSpeed()` | `int` | EV per tick while crafting |
| `getDrainSpeed()` | `int` | Progress lost per tick when out of EV |
| `shouldCopyInputComponents()` | `boolean` | Whether `assemble` copies input components |
| `matches(AraVitaeInput, Level)` | `boolean` | Ingredient and tier match |
| `assemble(AraVitaeInput, HolderLookup.Provider)` | `ItemStack` | Output, with optional component copy |
| `getResultItem(HolderLookup.Provider)` | `ItemStack` | Copy of the base result |
| `canCraftInDimensions(int, int)` | `boolean` | Always `true` |
| `getSerializer()` | `RecipeSerializer<?>` | Abstract; supplied by subclasses |
| `getType()` | `RecipeType<?>` | Abstract; supplied by subclasses |

JSON:

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

### AraVitaeInput

**Package** `com.breakinblocks.neovitae.api.recipe`

```java
public class AraVitaeInput implements RecipeInput {
    public AraVitaeInput(ItemStack inputStack, int altarTier);
    public ItemStack getItem(int index);
    public int getAltarTier();
    public int size();
}
```

---

## Altar Rune System

Altar runes grant the Ara Vitae stat bonuses. Built-in types are `EnumAltarRuneType` constants; custom types implement `IAltarRuneType` and register via `IAltarRuneRegistry`.

### IAltarRuneType

**Package** `com.breakinblocks.neovitae.api.altar.rune`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getId()` | `ResourceLocation` | Unique identifier |
| `getSerializedName()` | `String` | Lowercase identifier for NBT/JSON |

### EnumAltarRuneType

**Package** `com.breakinblocks.neovitae.api.altar.rune`

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
| `CHARGING` | Enables and increases stored charge |

Static helper: `fromSerializedName(String)` returns the matching constant or `null`.

### IAltarRuneRegistry

**Package** `com.breakinblocks.neovitae.api.altar.rune`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `registerRuneType(IAltarRuneType)` | `void` | Registers a custom rune type |
| `getRuneType(ResourceLocation)` | `@Nullable IAltarRuneType` | Lookup by ID |
| `getRuneTypeByName(String)` | `@Nullable IAltarRuneType` | Lookup by name |
| `getAllRuneTypes()` | `Collection<IAltarRuneType>` | All registered types |
| `isRegistered(ResourceLocation)` | `boolean` | Whether the ID is registered |
| `registerRuneBlock(Block, IAltarRuneType, int amount)` | `void` | Associates a block with a rune type |
| `getRunesForBlock(Block)` | `Map<IAltarRuneType, Integer>` | All rune contributions for a block |
| `hasRunes(Block)` | `boolean` | Whether the block contributes any runes |

```java
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

Getters: `getCapacityMod()`, `getTickRate()`, `getConsumptionMod()`, `getSacrificeMod()`, `getSelfSacrificeMod()`, `getDislocationMod()`, `getOrbCapacityMod()`, `getChargeAmountMod()`, `getChargeCapacityMod()`, `getEfficiencyMod()`.

Mutators (all return `void`, none are chainable):

| Method | Description |
|--------|-------------|
| `addCapacityMod(float)` / `multiplyCapacityMod(float)` | Capacity adjustments |
| `adjustTickRate(int)` / `setTickRate(int)` | Tick rate adjustments (minimum 1) |
| `addConsumptionMod(float)` | Crafting-speed bonus |
| `addSacrificeMod(float)` / `addSelfSacrificeMod(float)` | Sacrifice bonuses |
| `multiplyDislocationMod(float)` / `addDislocationMod(float)` | Dislocation adjustments |
| `addOrbCapacityMod(float)` | Orb capacity bonus |
| `addChargeAmountMod(float)` / `addChargeCapacityMod(float)` | Charging adjustments |
| `multiplyEfficiencyMod(float)` | Efficiency multiplier |
| `reset()` | Reset to defaults |

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

### Worked example: dynamic mana rune

```java
@SubscribeEvent
public static void onCalculateStats(AltarRuneEvent.CalculateStats event) {
    List<ManaRuneBlockEntity> manaRunes =
        event.getRuneBlockEntities(ManaRuneBlockEntity.class);

    int powered = 0, unpowered = 0;
    for (ManaRuneBlockEntity rune : manaRunes) {
        if (rune.hasMana()) powered++; else unpowered++;
    }

    AltarRuneModifiers mods = event.getModifiers();
    if (powered > 0) {
        mods.addCapacityMod(0.20f * powered);
        mods.multiplyEfficiencyMod(1.0f + 0.15f * powered);
    }
    if (unpowered > 0) {
        mods.addCapacityMod(0.05f * unpowered);
    }
}
```

---

## Ritual System

For player-facing mechanics see [Rituals](Rituals).

### IRitual

**Package** `com.breakinblocks.neovitae.api.ritual`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `performRitual(IMasterRitualStone)` | `void` | Run each refresh |
| `getRefreshCost()` | `int` | EV per refresh |
| `getRefreshTime()` | `int` | Ticks between refreshes |
| `gatherComponents(Consumer<RitualComponent>)` | `void` | Defines the rune structure |
| `getNewCopy()` | `IRitual` | Fresh instance |
| `activateRitual(IMasterRitualStone, Player, UUID owner)` | `boolean` | Called on activation |
| `stopRitual(IMasterRitualStone, BreakType)` | `void` | Called on end |
| `getName()` | `String` | Unique name |
| `getCrystalLevel()` | `int` | Required activation crystal tier |
| `getActivationCost()` | `int` | EV to activate |
| `getTranslationKey()` | `String` | Lang key |
| `provideInformationOfRitualToPlayer(Player)` | `Component[]` | Player-info lines |
| `getBlockRange(String)` | `AreaDescriptor` | Named range |
| `getListOfRanges()` | `List<String>` | All modifiable range keys |
| `getModifiableRanges()` | `Map<String, AreaDescriptor>` | All ranges by key |
| `readFromNBT(CompoundTag)` | `void` | Load |
| `writeToNBT(CompoundTag)` | `void` | Save |

`BreakType` enum: `DEACTIVATE`, `BREAK_MRS`, `BREAK_STONE`, `ACTIVATE`, `REDSTONE`, `EXPLOSION`.

### IImperfectRitual

**Package** `com.breakinblocks.neovitae.api.ritual`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `onActivate(IImperfectRitualStone, Player)` | `boolean` | Performs the ritual |
| `getName()` | `String` | Unique name |
| `getBlockRequirement()` | `Predicate<BlockState>` | Required block above the stone |
| `getActivationCost()` | `int` | EV cost |
| `isLightShow()` | `boolean` | Show lightning effect |
| `getTranslationKey()` | `String` | Lang key |

### IImperfectRitualStone

**Package** `com.breakinblocks.neovitae.api.ritual`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getRitualWorld()` | `Level` | World |
| `getRitualPos()` | `BlockPos` | Position |

### Master Ritual Stone

`IMasterRitualStone` lives outside the API package, at `com.breakinblocks.neovitae.ritual.IMasterRitualStone`, because it returns concrete `Ritual` references. Addons receive instances through the API methods on `IRitual`.

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
| `BLANK` | Gray | Basic rune |
| `WATER` | Aqua | Water elemental |
| `FIRE` | Red | Fire elemental |
| `EARTH` | Green | Earth elemental |
| `AIR` | White | Air elemental |
| `DUSK` | Dark Gray | Dusk rune |
| `DAWN` | Gold | Dawn rune |

Public fields `colorCode` (`ChatFormatting`), `translationKey`, `bookColor`. Static helper `byMetadata(int)`.

### AreaDescriptor

**Package** `com.breakinblocks.neovitae.api.ritual`

Abstract base. Abstract methods: `resetCache()`, `isWithinArea(BlockPos)`, `getContainedPositions(BlockPos)`, `getAABB(BlockPos)`, `modifyAreaByBlockPositions(BlockPos, BlockPos)`, `isWithinRange(BlockPos, BlockPos, int, int)`, `saveToNBT(CompoundTag)`, `loadFromNBT(CompoundTag)`, `copy()`, `intersects(AreaDescriptor)`, `offset(BlockPos)`.

Legacy aliases `writeToNBT` / `readFromNBT` delegate to the `saveToNBT` / `loadFromNBT` pair.

| Concrete | Constructors |
|----------|--------------|
| `Rectangle` | `(BlockPos min, BlockPos max)`, `(BlockPos offset, int sizeX, int sizeY, int sizeZ)`, `createCenteredAt(BlockPos, int radius, int height)` |
| `HemiSphere` | `(BlockPos centerOffset, int radius)` |
| `Cross` | `(BlockPos centerOffset, int length, int height)` |

`Rectangle` also exposes `CODEC` for use with datapack-driven configuration.

---

## Sigil System

For player-facing mechanics see [Sigils](Sigils).

### ISigilEffect

**Package** `com.breakinblocks.neovitae.api.sigil`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `codec()` | `MapCodec<? extends ISigilEffect>` | Codec for serialization dispatch |
| `useOnAir(Level, Player, ItemStack)` | `boolean` (default `false`) | Right-click in air |
| `useOnBlock(Level, Player, ItemStack, BlockPos, Direction, Vec3)` | `boolean` (default `false`) | Right-click on a block |
| `useOnEntity(Level, Player, ItemStack, Entity)` | `boolean` (default `false`) | Right-click on an entity |
| `activeTick(Level, Player, ItemStack, int slot, boolean isSelected)` | `void` (default no-op) | Tick while a toggleable sigil is on |
| `isToggleable()` | `boolean` (default `false`) | Whether the effect can be toggled |
| `onPlayerLogout(UUID, MinecraftServer)` | `void` (default no-op) | Per-effect logout cleanup |

`ISigilEffect.DISPATCH_CODEC` is the dispatch-codec holder; addons normally do not touch it directly.

### SigilEffect

**Package** `com.breakinblocks.neovitae.api.sigil`

Specialization of `ISigilEffect` used by NeoVitae's built-in effects. `SigilEffect.codec()` is typed as `MapCodec<? extends SigilEffect>`. The static field `SigilEffect.CODEC` is deprecated.

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

Statics: `DEFAULT_DRAIN_INTERVAL` (100), `CODEC`, `CLIENT_CODEC`, `HOLDER_CODEC`, `HOLDER_STREAM_CODEC`, `descriptionId(ResourceKey<SigilType>)`, `simple(int, ISigilEffect)`, `toggleable(int, int, ISigilEffect)`, `toggleableWithUse(int, int, int, ISigilEffect)`.

Instance methods: `isToggleable()`, `getCostForContext(UseContext)`; the `UseContext` enum is `AIR`, `BLOCK`, `ENTITY`, `ACTIVE`.

### SigilEffects (built-in registrations)

**Package** `com.breakinblocks.neovitae.api.sigil.effects`

| Effect type | Toggleable | Behavior |
|-------------|------------|----------|
| `neovitae:air` | Yes | Creative flight |
| `neovitae:place_fluid` | No | Places water or lava |
| `neovitae:void` | No | Voids fluids |
| `neovitae:fast_miner` | Yes | Haste |
| `neovitae:green_grove` | Yes | Crop growth, bone meal on use |
| `neovitae:magnetism` | Yes | Pulls items and XP orbs |
| `neovitae:frost` | Yes | Freezes water below the player |
| `neovitae:suppression` | Yes | Suppresses fluids |
| `neovitae:phantom_bridge` | Yes | Phantom blocks below the player |
| `neovitae:divination` | No | Shows anima info |
| `neovitae:blood_light` | No | Places light blocks |
| `neovitae:teleposition` | No | Teleports to a bound teleposer |
| `neovitae:necromancy` | No | Necromancy effect |
| `neovitae:bound_treasures` | No | Bound-treasures effect |

### Creating a custom sigil effect

```java
public record MyCustomEffect(int power) implements ISigilEffect {
    public static final MapCodec<MyCustomEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.INT.optionalFieldOf("power", 5).forGetter(MyCustomEffect::power)
        ).apply(instance, MyCustomEffect::new)
    );

    @Override public MapCodec<? extends ISigilEffect> codec() { return CODEC; }

    @Override
    public boolean useOnAir(Level level, Player player, ItemStack stack) {
        if (level.isClientSide) return false;
        player.heal(power);
        return true;
    }
}
```

```java
public static final DeferredRegister<MapCodec<? extends ISigilEffect>> SIGIL_EFFECTS =
    DeferredRegister.create(NeoVitaeRegistries.SIGIL_EFFECT_TYPE_KEY, "mymod");

public static final Supplier<MapCodec<MyCustomEffect>> MY_CUSTOM_EFFECT =
    SIGIL_EFFECTS.register("my_custom_effect", () -> MyCustomEffect.CODEC);
```

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

> Sigil-item registration uses `SigilTypeRegistry.SIGIL_TYPE_KEY` from `com.breakinblocks.neovitae.registry`, not from `NeoVitaeRegistries`.

---

## Sentient Armor System

For player-facing mechanics see [Sentient Armor](Sentient-Armor). Sentient Armor (formerly Sentient Armor) gains experience and levels up upgrades as the wearer performs actions.

### ISentientArmorManager

**Package** `com.breakinblocks.neovitae.api.sentient`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `hasFullSet(Player)` | `boolean` | Full Sentient Armor set check |
| `getChestPiece(Player)` | `ItemStack` | Chest piece or EMPTY |
| `getUpgrades(Player)` | `List<UpgradeInfo>` | All upgrades on the chest piece |
| `getUpgradeLevel(Player, ResourceLocation)` | `int` | Level of a specific upgrade |
| `grantUpgradeExperience(Player, ResourceLocation, float)` | `boolean` | Grants XP to an upgrade |
| `getUpgradeExperience(Player, ResourceLocation)` | `float` | Current XP on an upgrade |
| `getUsedUpgradePoints(Player)` | `int` | Points in use |
| `getMaxUpgradePoints()` | `int` | Default maximum (typically 100) |
| `getMaxUpgradePoints(Player)` | `int` | Effective max for the player's current armor |
| `getAvailableUpgradePoints(Player)` | `int` | Remaining points |

`UpgradeInfo` is a nested record:

```java
public record UpgradeInfo(ResourceLocation upgradeId, int level, float experience, int pointCost) {}
```

### ISentientArmorUpgrade

**Package** `com.breakinblocks.neovitae.api.sentient`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getMaxLevel()` | `int` | Maximum level |
| `getLevelFromExp(float)` | `int` | Level for an XP amount |
| `getExpForNextLevel(int)` | `float` | XP needed for the next level (`0` at max) |
| `getTotalExpForLevel(int)` | `float` | Total XP to reach a level |
| `getPointCost(int level)` | `int` | Point cost at a level |
| `getEffects()` | `DataComponentMap` | Components applied while active |

### IUpgradeHolder

**Package** `com.breakinblocks.neovitae.api.item`

Marker interface implemented by Sentient Armor item classes.

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getMaxUpgradePoints(ItemStack, Player)` | `int` | Effective max points |
| `hasFullSentientArmorSet(Player)` | `boolean` | Full-set check |
| `isInvalidArmor(ItemStack)` | `boolean` | "Dead" / invalid check |

```java
ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
if (chest.getItem() instanceof IUpgradeHolder holder) {
    int max = holder.getMaxUpgradePoints(chest, player);
}
```

---

## Tranquility / Incense

Tranquility powers the Incense Altar's bonus multiplier during self-sacrifice.

### ITranquilityHandler

**Package** `com.breakinblocks.neovitae.api.incense`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getTranquilityType(Block)` | `@Nullable EnumTranquilityType` | Aspect type for a block |
| `getTranquilityType(BlockState)` | `@Nullable EnumTranquilityType` | Same, by block state |
| `getTranquilityValue(Block)` | `double` | Numeric value, or `0` |
| `getTranquilityValue(BlockState)` | `double` | Same, by block state |
| `hasTranquility(Block)` | `boolean` | Whether the block has any entry |
| `hasTranquility(BlockState)` | `boolean` | Same, by block state |

`EnumTranquilityType` constants (`PLANT`, `CROP`, `TREE`, `EARTHEN`, `WATER`, `FIRE`, `LAVA`) live at `com.breakinblocks.neovitae.incense.EnumTranquilityType`.

### TranquilityHandler

**Package** `com.breakinblocks.neovitae.api.incense`

Default implementation; `TranquilityHandler.INSTANCE`. Addons normally use the API accessor.

### Datapack format

```json
{
  "values": {
    "#minecraft:logs": { "type": "tree", "value": 1.0 },
    "mymod:magic_flower": { "type": "plant", "value": 2.0 }
  }
}
```

When a block matches multiple entries the highest value wins.

---

## Routing System

The routing system is Neo Vitae's item, fluid, and energy routing network. Nodes discover a master, which iterates registered `RoutingChannel` implementations each tick to transfer between input and output sides.

### IRoutingNode

**Package** `com.breakinblocks.neovitae.api.routing`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `connectMasterToRemainingNode(Level, List<BlockPos>, IMasterRoutingNode)` | `void` | Propagate the master |
| `getCurrentBlockPos()` | `BlockPos` | Node position |
| `getConnected()` | `List<BlockPos>` | Adjacent nodes |
| `getMasterPos()` | `BlockPos` | Master position |
| `isConnectionEnabled(BlockPos)` | `boolean` | Connection-enabled check |
| `isMaster(IMasterRoutingNode)` | `boolean` | Master-binding check |
| `addConnection(BlockPos)` / `removeConnection(BlockPos)` / `removeAllConnections()` | `void` | Connection management |
| `checkAndPurgeConnectionToMaster(BlockPos ignorePos)` | `List<BlockPos>` | Purges stale master connections |
| `recheckConnectionToMaster(List<BlockPos>, List<IRoutingNode>)` | `Triple<Boolean, List<BlockPos>, List<IRoutingNode>>` | Re-discovers the master |

### IMasterRoutingNode

**Package** `com.breakinblocks.neovitae.api.routing`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `isConnected(List<BlockPos>, BlockPos)` | `boolean` | Path exists to a node |
| `addNodeToList(IRoutingNode)` | `void` | Track a node |
| `addConnections(BlockPos, List<BlockPos>)` | `void` | Bulk add |
| `addConnection(BlockPos, BlockPos)` | `void` | Bidirectional add |
| `removeConnection(BlockPos, BlockPos)` | `void` | Bidirectional remove |
| `removeNodeFromGraph(BlockPos)` | `void` | Scrubs a node entirely |

### Item routing

| Interface | Methods added |
|-----------|---------------|
| `IItemRoutingNode` | `isInventoryConnectedToSide(Direction)`, `getPriority(Direction)` |
| `IInputItemRoutingNode` | `isInput(Direction)`, `getInputFilterForSide(Direction)` |
| `IOutputItemRoutingNode` | `isOutput(Direction)`, `getOutputFilterForSide(Direction)` |

### Fluid routing

| Interface | Methods added |
|-----------|---------------|
| `IFluidRoutingNode` | `isTankConnectedToSide(Direction)`, `getFluidPriority(Direction)` |
| `IInputFluidRoutingNode` | `isFluidInput(Direction)`, `getInputFluidFilterForSide(Direction)` |
| `IOutputFluidRoutingNode` | `isFluidOutput(Direction)`, `getOutputFluidFilterForSide(Direction)` |

### Filters

`IRoutingFilter` is the base marker (with optional `getNodePos()` for visual hooks).

| Filter | Notes |
|--------|-------|
| `IItemFilter` | `initializeFilter(...)`, `transferStackThroughOutputFilter`, `transferThroughInputFilter`, `doesStackPassFilter`, `doStacksMatch`, `getFilterList` |
| `IFluidFilter` | Mirrors `IItemFilter` for `FluidStack` and `IFluidHandler` |
| `IEnergyFilter` | `transferEnergyThroughOutputFilter`, `transferThroughInputFilter` |
| `IFilterKey` | `doesStackMatch`, `getCount`, `setCount`, `grow`, `shrink`, `isEmpty` |

### RoutingChannel and RoutingChannelRegistry

| Method | Return Type | Description |
|--------|-------------|-------------|
| `RoutingChannel.id()` | `String` | NBT/persistence key |
| `isInputNode(BlockEntity)` / `isOutputNode(BlockEntity)` | `boolean` | Eligibility checks |
| `isConnectedOnSide(BlockEntity, Direction)` | `boolean` | Capability presence |
| `isInputSide(...)` / `isOutputSide(...)` | `boolean` | I/O side check |
| `getPriority(BlockEntity, Direction)` | `int` | Side priority |
| `getInputFilter(...)` / `getOutputFilter(...)` | `@Nullable F` | Filter lookup |
| `getMaxTransfer(BlockEntity masterNode)` | `int` | Per-tick cap |
| `transfer(F, F, int)` | `int` | Actual transfer |

```java
RoutingChannelRegistry.register(new MyCustomChannel());
List<RoutingChannel<?>> channels = RoutingChannelRegistry.getChannels();
```

---

## Stream Effects

`StreamEffect` is a server-driven, client-rendered visual energy stream or stationary blob.

### StreamEffect

**Package** `com.breakinblocks.neovitae.api.stream`

Static factories: `builder(double, double, double)`, `builder(Entity)`, `builder(BlockPos)`, `decode(FriendlyByteBuf)`. Instance methods: `sendToNearby(ServerLevel, BlockPos, double)`, `sendToNearby(ServerLevel, double)`, `encode(FriendlyByteBuf)`. Public final fields cover all renderable parameters (`color`, `scale`, `speed`, `gravity`, `wobbleAmplitude`, etc.).

`Builder` exposes fluent setters: `to(double,double,double)` / `to(BlockPos)` / `to(Entity)` / `toTracked(Entity)`, `color`, `scale`, `alphaStart`, `alphaEnd`, `alpha`, `glow`, `tubeSegments`, `speed`, `gravity`, `wobble`, `wobbleFrequency`, `spiralInto`, `spiralRadius`, `spiralSpeed`, `approachHeight`, `lifetime`, `drainSpeed`, `stationary`, `blockyMode`, `trailDensity`, `rawTrailColor`, then `build()`.

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

Enum: `NONE`, `BLOCKY`, `BLOCKY_STEPS`, `BLOCKY_UNIFORM`, `BLOCKY_BOX`, `BLOCKY_BEAM`.

### StreamPresets

**Package** `com.breakinblocks.neovitae.api.stream`

Pre-tuned `Builder` recipes; each method returns a `StreamEffect.Builder` that callers can further customize before `build()`.

| Preset | Description |
|--------|-------------|
| `bloodTendril(Entity/BlockPos, BlockPos)` | Viscous crimson tendril |
| `soulSiphon(Entity/BlockPos, BlockPos)` | Thin ghostly wisp |
| `voidTendril(Entity/BlockPos, BlockPos)` | Thick near-black mass |
| `lifePulse(Entity/BlockPos, BlockPos)` | Warm golden surge |
| `demonTether(Entity/BlockPos, BlockPos)` | Fiery orange chain |
| `corruptionSeep(Entity/BlockPos, BlockPos)` | Sickly green ooze |
| `arcaneBolt(Entity/BlockPos, BlockPos)` | Saturated purple thread |
| `blockBolt` / `blockBolt2` … `blockBolt6` | Voxel-styled bolts (`BlockyMode` variants) |
| `emberMote(BlockPos)` | Stationary floating ember |
| `soulWisp(BlockPos)` | Stationary pale wisp |
| `voidMark(BlockPos)` | Stationary heavy void stain |

---

## Custom Player Attributes

Neo Vitae registers custom player attributes that addon mods can apply modifiers to via equipment, effects, or data packs.

**Package** `com.breakinblocks.neovitae.common.attribute.NVAttributes`

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

```java
double siphon = player.getAttributeValue(NVAttributes.BLOOD_SIPHON);

player.getAttribute(NVAttributes.BONUS_SACRIFICE).addTransientModifier(
    new AttributeModifier(
        ResourceLocation.fromNamespaceAndPath("mymod", "sacrifice_bonus"),
        25.0,
        AttributeModifier.Operation.ADD_VALUE
    )
);
```

### Blood Siphon

EV gained = min(attribute_value, damage_dealt) × multiplier.
- vs Players, multiplier defaults to 100 and drains from the target's anima.
- vs Mobs, multiplier defaults to 10 and EV is generated from nothing.

### Blood Shield

Damage reduction = 10% per attribute point (cap 99%). EV cost = damage_prevented × configurable multiplier (default 100). If there isn't enough EV the shield absorbs partially and the remainder passes through.

### Server Configuration

In `config/neovitae-server.toml` under `[blood_attributes]`:
- `siphon_player_multiplier` (default 100)
- `siphon_mob_multiplier` (default 10)
- `shield_lp_cost_multiplier` (default 100)

---

## Events

Neo Vitae fires NeoForge events on the game event bus.

### AnimaEvent

**Package** `com.breakinblocks.neovitae.api.event`

Base getters: `getNetwork()`, `getOwnerId()`, `getTicket()`, `getAmount()`.

- `AnimaEvent.PreSyphon` (cancellable) `getModifiedAmount()` / `setModifiedAmount(int)`
- `AnimaEvent.PostSyphon` `getActualAmount()`
- `AnimaEvent.PreAdd` (cancellable) `getMaximum()`, `getModifiedAmount()`, `setModifiedAmount(int)`
- `AnimaEvent.PostAdd` `getMaximum()`, `getActualAmount()`

```java
@SubscribeEvent
public void onPreSyphon(AnimaEvent.PreSyphon event) {
    event.setModifiedAmount((int) (event.getAmount() * 0.9));
}
```

### AltarRuneEvent

**Package** `com.breakinblocks.neovitae.api.event`

Base getters: `getAltar()`, `getLevel()`, `getPos()`, `getTier()`.

- `AltarRuneEvent.GatherRunes` `getRuneCounts()`, `addRunes(IAltarRuneType, int)`, `getRuneInstances()`
- `AltarRuneEvent.CalculateStats` `getModifiers()`, `getRuneCounts()`, `getRuneCount(IAltarRuneType)`, `getRuneInstances()`, `getRuneBlockEntities(Class<T>)`, `getRuneInstancesByType(IAltarRuneType)`
- `AltarRuneEvent.PostCalculate` `getFinalModifiers()`, `getRuneInstances()`

### SentientArmorEvent

**Package** `com.breakinblocks.neovitae.api.event`

Base getters: `getWearer()`, `getArmorPiece()`.

- `SentientArmorEvent.ExperienceGain` (cancellable) `getUpgradeId()`, `getExperience()`, `setExperience(float)`
- `SentientArmorEvent.LevelUp` `getUpgradeId()`, `getPreviousLevel()`, `getNewLevel()`

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

Capabilities are registered automatically by Neo Vitae; addons just query them.

```java
IAraVitae altar = level.getCapability(NVCapabilities.ARA_VITAE, pos, null);
```

---

## Registry Keys

### NeoVitaeRegistries

**Package** `com.breakinblocks.neovitae.api.registry`

| Key | Type | Description |
|-----|------|-------------|
| `RITUAL_KEY` | `ResourceKey<Registry<IRitual>>` | Regular rituals |
| `IMPERFECT_RITUAL_KEY` | `ResourceKey<Registry<IImperfectRitual>>` | Imperfect rituals |
| `SIGIL_EFFECT_TYPE_KEY` | `ResourceKey<Registry<MapCodec<? extends ISigilEffect>>>` | Sigil effect type registry |

> The sigil **type** datapack registry key is `SigilTypeRegistry.SIGIL_TYPE_KEY` in `com.breakinblocks.neovitae.registry`.

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

## See Also

- [API Overview](API-Overview) - Setup, packaging, and quick examples.
- [Rituals](Rituals), [Sigils](Sigils), [Sentient Armor](Sentient-Armor) - Player-facing gameplay docs.
- [Home](Home) - Wiki index.
