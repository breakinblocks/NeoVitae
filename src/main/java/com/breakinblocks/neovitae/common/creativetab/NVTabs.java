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
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;
import com.breakinblocks.neovitae.common.datacomponent.UpgradeTome;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.living.LivingHelper;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;
import com.breakinblocks.neovitae.common.registry.NVRegistries;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.function.Consumer;

public class NVTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NeoVitae.MODID);

    public static final Holder<CreativeModeTab> MAIN = TABS.register(
            "main",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(NVBlocks.BLOOD_ALTAR))
                    .title(Component.translatable("item_group.neovitae.main"))
                    .displayItems((parameters, output) -> {
                        addAll(NVBlocks.BLOCK_ITEMS, output::accept);

                        ItemStack living_plate = new ItemStack(NVItems.LIVING_PLATE);
                        LivingHelper.setDefaultLiving(living_plate, parameters.holders());
                        output.accept(living_plate);

                        addAll(NVItems.BASIC_ITEMS, output::accept);
                        addAll(NVItems.ITEMS, output::accept);
                        addAll(NVFluids.BUCKETS, output::accept);
                        NVItems.WILL_ITEMS.getEntries().forEach(holder -> {
                            String path = holder.getId().getPath();
                            // Monster souls are already typed (basemonstersoul_*), don't create variants
                            if (path.startsWith("basemonstersoul")) {
                                // Just add the item with 5 will amount
                                ItemStack stack = new ItemStack(holder.get());
                                stack.set(NVDataComponents.DEMON_WILL_AMOUNT, 5.0);
                                output.accept(stack);
                            } else {
                                // Soul gems and raw will get variants for each will type, filled with max will
                                double maxWill = getMaxWillForItem(path);
                                for (EnumWillType type : EnumWillType.values()) {
                                    ItemStack stack = new ItemStack(holder.get());
                                    stack.set(NVDataComponents.DEMON_WILL_TYPE, type);
                                    stack.set(NVDataComponents.DEMON_WILL_AMOUNT, maxWill);
                                    output.accept(stack);
                                }
                            }
                        });

                        // Add empty default will variants for tartaric gems
                        addEmptyGem(output::accept, NVItems.SOUL_GEM_PETTY.get());
                        addEmptyGem(output::accept, NVItems.SOUL_GEM_LESSER.get());
                        addEmptyGem(output::accept, NVItems.SOUL_GEM_COMMON.get());
                        addEmptyGem(output::accept, NVItems.SOUL_GEM_GREATER.get());
                        addEmptyGem(output::accept, NVItems.SOUL_GEM_GRAND.get());

                        addAll(NVBlocks.BASIC_BLOCK_ITEMS, output::accept);
                        addAll(DungeonBlocks.ITEMS, output::accept);
                    })
                    .build()
    );

    public static final Holder<CreativeModeTab> TOMES = TABS.register(
            "tomes",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(NVItems.UPGRADE_TOME))
                    .title(Component.translatable("item_group.neovitae.tomes"))
                    .displayItems((params, output) -> {
                        addAll(params.holders().lookupOrThrow(NVRegistries.Keys.LIVING_UPGRADES).get(NVTags.Living.TOOLTIP_ORDER).orElseThrow(), output::accept);
                    })
                    .build()
    );

    public static final Holder<CreativeModeTab> TRAINERS = TABS.register(
            "trainers",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(NVItems.UPGRADE_TOME))
                    .title(Component.translatable("item_group.neovitae.trainers"))
                    .displayItems((params, output) -> {
                        addAll(params.holders().lookupOrThrow(NVRegistries.Keys.LIVING_UPGRADES).get(NVTags.Living.TRAINERS).orElseThrow(), output::accept);
                    })
                    .build()
    );

    private static void addAll(HolderSet<LivingUpgrade> set, Consumer<ItemStack> tab) {
        ItemStack tome = new ItemStack(NVItems.UPGRADE_TOME);
        set.forEach(upgrade -> {
            upgrade.value().levels().expToLevel().forEach((exp, cost) -> {
                tome.set(NVDataComponents.UPGRADE_TOME_DATA, new UpgradeTome(upgrade, exp));
                tab.accept(tome.copy());
            });
        });
    }

    private static void addAll(DeferredRegister<Item> register, Consumer<ItemStack> tab) {
        register.getEntries().forEach(holder -> {
            tab.accept(new ItemStack(holder.getDelegate()));
        });
    }

    private static void addEmptyGem(Consumer<ItemStack> tab, Item gem) {
        ItemStack stack = new ItemStack(gem);
        stack.set(NVDataComponents.DEMON_WILL_AMOUNT, 0.0);
        stack.set(NVDataComponents.DEMON_WILL_TYPE, EnumWillType.DEFAULT);
        tab.accept(stack);
    }

    private static double getMaxWillForItem(String path) {
        // Max will amounts for each gem tier (from data maps)
        return switch (path) {
            case "soul_gem_petty" -> 64.0;
            case "soul_gem_lesser" -> 256.0;
            case "soul_gem_common" -> 1024.0;
            case "soul_gem_greater" -> 4096.0;
            case "soul_gem_grand" -> 16384.0;
            case "raw_will" -> 50.0; // Raw will max amount
            default -> 0.0;
        };
    }

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }
}
