# API Overview

The Neo Vitae API lets addon mods interact with Neo Vitae's core systems including Animas, Ara Vitaes, Altar Runes, Rituals, Sigils, and Sentient Armor. It is intended for **mod developers** who want to extend, modify, or integrate with Neo Vitae from their own mod.

If you're a player looking for in-game mechanics rather than code, start at the [Home](Home) page and the per-system player guides such as [Rituals](Rituals), [Sigils](Sigils), and [Sentient Armor](Sentient-Armor).

For complete signatures, types, and method-by-method details, see [API Reference](API-Reference).

---

## Getting Started

### Adding the API Dependency

Add Neo Vitae as a dependency in your `build.gradle`:

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

> **Note:** The API classes are in the main mod JAR under `com.breakinblocks.neovitae.api`. There is no separate api artifact.

---

## API Package Structure

All API classes live in the main source set at `src/main/java/com/breakinblocks/neovitae/api/`. There is no separate API artifact or source set; you compile against the main jar and the same classes are loaded at runtime.

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

These are in `com.breakinblocks.neovitae.common` (not the API package) but might be used by addons:

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

## Quick Examples

The patterns below cover the most common addon entry points. Each links to its full reference section in [API Reference](API-Reference).

### Accessing the API

```java
import com.breakinblocks.neovitae.api.NeoVitaeAPI;
import com.breakinblocks.neovitae.api.INeoVitaeAPI;

// Get the API instance (safe to call from FMLCommonSetupEvent or later)
INeoVitaeAPI api = NeoVitaeAPI.getInstance();
```

### Reading and Modifying a Player's Anima

See the [Anima System](API-Reference#anima-system) reference for full details, including `AnimaTicket` and the `SyphonResult` record.

```java
INeoVitaeAPI api = NeoVitaeAPI.getInstance();

IAnima anima = api.getAnima(player.getUUID());
if (anima != null) {
    int currentEV = anima.getCurrentEV();
}

AnimaTicket ticket = AnimaTicket.create(100);
SyphonResult result = anima.syphonAndDamage(player, ticket);
if (result.success()) {
    performSigilEffect();
}
```

### Registering a Custom Ritual

See the [Ritual System](API-Reference#ritual-system) reference for `IRitual`, `RitualComponent`, and `AreaDescriptor` details. Players-facing ritual info lives in [Rituals](Rituals).

```java
public static final DeferredRegister<Ritual> RITUALS =
    DeferredRegister.create(NeoVitaeRegistries.RITUAL_KEY, "yourmodid");

public static final DeferredHolder<Ritual, MyCustomRitual> MY_RITUAL =
    RITUALS.register("my_ritual", MyCustomRitual::new);

public YourMod(IEventBus modBus) {
    RITUALS.register(modBus);
}
```

### Registering a Custom Sigil Effect

See the [Sigil System](API-Reference#sigil-system) reference for the full effect/JSON pipeline. Player-facing sigil docs live in [Sigils](Sigils).

```java
public static final DeferredRegister<MapCodec<? extends ISigilEffect>> SIGIL_EFFECTS =
        DeferredRegister.create(NeoVitaeRegistries.SIGIL_EFFECT_TYPE_KEY, "mymod");

public static final Supplier<MapCodec<MyCustomEffect>> MY_CUSTOM_EFFECT =
        SIGIL_EFFECTS.register("my_custom_effect", () -> MyCustomEffect.CODEC);

public static void register(IEventBus modBus) {
    SIGIL_EFFECTS.register(modBus);
}
```

### Querying Sentient Armor

See the [Sentient Armor System](API-Reference#sentient-armor-system) reference for `ISentientArmorManager`, `UpgradeInfo`, and `IUpgradeHolder`. Player-facing info lives in [Sentient Armor](Sentient-Armor).

```java
ISentientArmorManager manager = api.getSentientArmorManager();
if (manager.hasFullSet(player)) {
    ResourceLocation upgradeId = ResourceLocation.fromNamespaceAndPath("neovitae", "strong_legs");
    int level = manager.getUpgradeLevel(player, upgradeId);
    manager.grantUpgradeExperience(player, upgradeId, 10.0f);
}
```

---

## Important Notes

- **Hellfire Forge** - Recipe type is `neovitae:hellfire_forge`, recipe directory is `hellfire_forge/`. Internal references use `HELLFIRE_FORGE_*` naming.
- **API classes in main JAR** - there is no separate API artifact or source set. Compile against the main jar; the same classes are loaded at runtime.
- **Modonomicon transitive dependency** - addons that compile against NeoVitae may need Modonomicon on the classpath (for `NVGuideBookItem`).
- **API stability** - The interfaces in `com.breakinblocks.neovitae.api.*` are the supported surface for addons. Classes outside that package (including the `common.*` helpers listed above) are used by addons in practice but are not held to the same stability guarantees and may shift between releases. Pin your dependency to a known-good version range.
- **`@OnlyIn` is not used in this API on 1.21.1 in a way that affects addons**, but if you backport patterns from this doc to the 26.1 port note that the annotation is removed there. Always gate client references through `client/` packages.
- **Reporting issues** - please include the Neo Vitae version, NeoForge version, relevant code snippets, and full error logs.

---

## See Also

- [API Reference](API-Reference) - Full per-system signatures, codecs, JSON formats, and worked examples.
- [Rituals](Rituals), [Sigils](Sigils), [Sentient Armor](Sentient-Armor) - Player-facing gameplay docs for the systems you'll be hooking into.
- [Home](Home) - Wiki index.
