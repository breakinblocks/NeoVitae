package com.breakinblocks.neovitae.common.creativetab;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.datacomponent.EffectHolder;
import com.breakinblocks.neovitae.common.datacomponent.FlaskEffects;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.item.potion.ItemAlchemyFlask;
import com.breakinblocks.neovitae.common.sentient.SentientHelper;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.common.material.MaterialRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class NVTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NeoVitae.MODID);

    public static final Holder<CreativeModeTab> MAIN = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(NVBlocks.ARA_VITAE))
                    .title(Component.translatable("item_group.neovitae.main"))
                    .displayItems((parameters, output) -> {
                        addAll(NVBlocks.BLOCK_ITEMS, output::accept);

                        ItemStack sentient_plate = new ItemStack(NVItems.SENTIENT_PLATE);
                        SentientHelper.setDefaultSentient(sentient_plate, parameters.holders());
                        output.accept(sentient_plate);

                        addAll(NVItems.BASIC_ITEMS, output::accept);
                        MaterialRegistry.getAllItems()
                                .forEach(holder -> output.accept(new ItemStack(holder.get())));
                        addFlaskVariants(output::accept);
                        addAll(NVItems.ITEMS, output::accept);
                        addAll(NVFluids.BUCKETS, output::accept);
                        NVItems.SPIRITUS_ITEMS.getEntries().forEach(holder -> {
                            String path = holder.getId().getPath();
                            // Monster souls are already typed (basemonstersoul_*), don't create variants
                            if (path.startsWith("base_spiritus_soul")) {
                                // Just add the item with 5 will amount
                                ItemStack stack = new ItemStack(holder.get());
                                stack.set(NVDataComponents.SPIRITUS_AMOUNT, 5.0);
                                output.accept(stack);
                            } else {
                                // Soul gems and raw Spiritus get variants for each will type, filled with max will
                                double maxSpiritus = getMaxWillForItem(path);
                                for (SpiritusType type : SpiritusType.values()) {
                                    ItemStack stack = new ItemStack(holder.get());
                                    stack.set(NVDataComponents.SPIRITUS_TYPE, type);
                                    stack.set(NVDataComponents.SPIRITUS_AMOUNT, maxSpiritus);
                                    output.accept(stack);
                                }
                            }
                        });

                        // Add empty default will variants for Spiritus Gems
                        addEmptyGem(output::accept, NVItems.SPIRITUS_GEM_PETTY.get());
                        addEmptyGem(output::accept, NVItems.SPIRITUS_GEM_LESSER.get());
                        addEmptyGem(output::accept, NVItems.SPIRITUS_GEM_COMMON.get());
                        addEmptyGem(output::accept, NVItems.SPIRITUS_GEM_GREATER.get());
                        addEmptyGem(output::accept, NVItems.SPIRITUS_GEM_GRAND.get());

                        addBloodTankVariants(output::accept);
                        addAll(NVBlocks.BASIC_BLOCK_ITEMS, output::accept);
                        addAll(DungeonBlocks.ITEMS, output::accept);
                        addAll(NVItems.ARRAY_ITEMS, output::accept);
                    })
                    .build()
    );

    public static final Holder<CreativeModeTab> TOMES = TABS.register(
            "tomes",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(NVItems.UPGRADE_TOME))
                    .title(Component.translatable("item_group.neovitae.tomes"))
                    .displayItems((params, output) -> {
                        addAll(params.holders().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES).get(NVTags.Sentient.TOOLTIP_ORDER).orElseThrow(), output::accept);
                    })
                    .build()
    );

    public static final Holder<CreativeModeTab> TRAINERS = TABS.register(
            "trainers",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(NVItems.UPGRADE_TOME))
                    .title(Component.translatable("item_group.neovitae.trainers"))
                    .displayItems((params, output) -> {
                        addAll(params.holders().lookupOrThrow(NVRegistries.Keys.SENTIENT_UPGRADES).get(NVTags.Sentient.TRAINERS).orElseThrow(), output::accept);
                    })
                    .build()
    );

    private static void addAll(HolderSet<SentientUpgrade> set, Consumer<ItemStack> tab) {
        ItemStack tome = new ItemStack(NVItems.UPGRADE_TOME);
        set.forEach(upgrade -> {
            upgrade.value().levels().expToLevel().forEach((exp, cost) -> {
                tome.set(NVDataComponents.UPGRADE_TOME_DATA, new UpgradeTome(upgrade, exp));
                tab.accept(tome.copy());
            });
        });
    }

    private static void addFlaskVariants(Consumer<ItemStack> tab) {
        List<Holder<MobEffect>> flaskEffects = List.of(
                MobEffects.MOVEMENT_SPEED,
                MobEffects.DAMAGE_BOOST,
                MobEffects.REGENERATION,
                MobEffects.HEAL,
                MobEffects.HARM,
                MobEffects.POISON,
                MobEffects.NIGHT_VISION,
                MobEffects.INVISIBILITY,
                MobEffects.FIRE_RESISTANCE,
                MobEffects.WATER_BREATHING,
                MobEffects.JUMP,
                MobEffects.SLOW_FALLING,
                MobEffects.MOVEMENT_SLOWDOWN,
                MobEffects.WEAKNESS,
                MobEffects.LEVITATION,
                NVMobEffects.BOUNCE.getDelegate(),
                NVMobEffects.FLIGHT.getDelegate(),
                NVMobEffects.GROUNDED.getDelegate(),
                NVMobEffects.GRAVITY.getDelegate(),
                NVMobEffects.SUSPENDED.getDelegate(),
                NVMobEffects.HARD_CLOAK.getDelegate(),
                NVMobEffects.OBSIDIAN_CLOAK.getDelegate(),
                NVMobEffects.HEAVY_HEART.getDelegate(),
                NVMobEffects.PASSIVITY.getDelegate(),
                NVMobEffects.SPECTRAL_SIGHT.getDelegate()
        );

        for (Holder<MobEffect> effect : flaskEffects) {
            ItemStack flask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
            FlaskEffects effects = FlaskEffects.single(EffectHolder.create(effect, 3600, 0));
            ItemAlchemyFlask.setFlaskEffects(flask, effects);
            tab.accept(flask);
        }

        // Combination flasks (multi-effect precursors for advanced potions)
        addCombinationFlask(tab, NVMobEffects.GROUNDED.getDelegate(), MobEffects.SLOW_FALLING);       // → Gravity
        addCombinationFlask(tab, NVMobEffects.GRAVITY.getDelegate(), MobEffects.HEAL);                 // → Heavy Heart
        addCombinationFlask(tab, NVMobEffects.SUSPENDED.getDelegate(), MobEffects.LEVITATION);         // → Flight
    }

    @SafeVarargs
    private static void addCombinationFlask(Consumer<ItemStack> tab, Holder<MobEffect>... effects) {
        ItemStack flask = new ItemStack(NVItems.ALCHEMY_FLASK.get());
        List<EffectHolder> holders = new ArrayList<>();
        for (Holder<MobEffect> effect : effects) {
            holders.add(EffectHolder.create(effect, 3600, 0));
        }
        ItemAlchemyFlask.setFlaskEffects(flask, new FlaskEffects(holders));
        tab.accept(flask);
    }

    private static void addBloodTankVariants(Consumer<ItemStack> tab) {
        for (int tier = 1; tier <= 16; tier++) {
            ItemStack stack = new ItemStack(NVBlocks.BLOOD_TANK.block().get());
            stack.set(NVDataComponents.CONTAINER_TIER, tier);
            tab.accept(stack);
        }
    }

    private static void addAll(DeferredRegister<Item> register, Consumer<ItemStack> tab) {
        register.getEntries().forEach(holder -> {
            tab.accept(new ItemStack(holder.getDelegate()));
        });
    }

    private static void addEmptyGem(Consumer<ItemStack> tab, Item gem) {
        ItemStack stack = new ItemStack(gem);
        stack.set(NVDataComponents.SPIRITUS_AMOUNT, 0.0);
        stack.set(NVDataComponents.SPIRITUS_TYPE, SpiritusType.RAW);
        tab.accept(stack);
    }

    private static double getMaxWillForItem(String path) {
        // Max will amounts for each gem tier (from data maps)
        return switch (path) {
            case "spiritus_gem_petty" -> 64.0;
            case "spiritus_gem_lesser" -> 256.0;
            case "spiritus_gem_common" -> 1024.0;
            case "spiritus_gem_greater" -> 4096.0;
            case "spiritus_gem_grand" -> 16384.0;
            case "raw_spiritus" -> 50.0; // Raw Spiritus max amount
            default -> 0.0;
        };
    }

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }
}
