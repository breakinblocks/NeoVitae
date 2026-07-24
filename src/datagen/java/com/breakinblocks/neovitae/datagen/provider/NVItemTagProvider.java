package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.world.level.block.Block;
import com.breakinblocks.neovitae.datagen.BlockGroups;

public class NVItemTagProvider extends ItemTagsProvider {
    public NVItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                             CompletableFuture<TagsProvider.TagLookup<Block>> blockTags) {
        super(output, lookupProvider, NeoVitae.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 26.1 removed ItemTagsProvider.copy() — inline the block-tag contents into the item tag manually.
        // NVTags.Blocks.STORAGE_BLOCKS_HELLFORGED contains BlockGroups.HELLFORGED_BLOCK (currently a single entry).
        tag(NVTags.Items.STORAGE_BLOCKS_HELLFORGED)
                .add(NVBlocks.HELLFORGED_BLOCK.item().get());

        tag(Tags.Items.STRINGS).add(Items.STRING);

        addBlockItems(NVTags.Items.RUNES, BlockGroups.RUNE_T1);
        addBlockItems(NVTags.Items.RUNES, BlockGroups.RUNE_T2);
        addBlockItems(NVTags.Items.BLOODSTONES, BlockGroups.BLOODSTONE);
        tag(NVTags.Items.T3_CAPSTONES).add(NVBlocks.BLOOD_STAINED_GLASS.item().get());
        addBlockItems(NVTags.Items.T4_CAPSTONES, BlockGroups.BLOODSTONE);
        tag(NVTags.Items.T5_CAPSTONES).add(NVBlocks.HELLFORGED_BLOCK.item().get());
        addBlockItems(NVTags.Items.T6_CAPSTONES, BlockGroups.CRYSTAL_CLUSTER);
        tag(NVTags.Items.ANIMA_COMPARATOR).add(NVBlocks.BLOOD_STAINED_GLASS.item().get());

        tag(NVTags.Items.SENTIENT_SET)
                .add(NVItems.SENTIENT_HELMET.get(), NVItems.SENTIENT_PLATE.get(), NVItems.SENTIENT_LEGGINGS.get(), NVItems.SENTIENT_BOOTS.get());

        tag(NVTags.Items.SENTIENT_ARMOR_REPAIR)
                .add(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get());

        tag(NVTags.Items.VITAE_STONE)
                .add(Items.DEEPSLATE);

        tag(NVTags.Items.SPIRITUS_GEM)
                .add(NVItems.SPIRITUS_GEM_PETTY.get())
                .add(NVItems.SPIRITUS_GEM_LESSER.get())
                .add(NVItems.SPIRITUS_GEM_COMMON.get())
                .add(NVItems.SPIRITUS_GEM_GREATER.get())
                .add(NVItems.SPIRITUS_GEM_GRAND.get());

        tag(NVTags.Items.CHARGES)
                .add(NVBlocks.SHAPED_CHARGE.item().get())
                .add(NVBlocks.AUG_SHAPED_CHARGE.item().get())
                .add(NVBlocks.SHAPED_CHARGE_DEEP.item().get())
                .add(NVBlocks.DEFORESTER_CHARGE.item().get())
                .add(NVBlocks.DEFORESTER_CHARGE_2.item().get())
                .add(NVBlocks.VEINMINE_CHARGE.item().get())
                .add(NVBlocks.VEINMINE_CHARGE_2.item().get())
                .add(NVBlocks.FUNGAL_CHARGE.item().get())
                .add(NVBlocks.FUNGAL_CHARGE_2.item().get());

        tag(NVTags.Items.SENTIENT_UPGRADE_SET)
                .addTag(NVTags.Items.SENTIENT_SET);

        tag(ItemTags.LECTERN_BOOKS).add(NVItems.GUIDE_BOOK.get());
        tag(ItemTags.BOOKSHELF_BOOKS).add(NVItems.GUIDE_BOOK.get());

        // Add living armor to vanilla armor tags for mod compatibility
        tag(ItemTags.HEAD_ARMOR).add(NVItems.SENTIENT_HELMET.get());
        tag(ItemTags.CHEST_ARMOR).add(NVItems.SENTIENT_PLATE.get());
        tag(ItemTags.LEG_ARMOR).add(NVItems.SENTIENT_LEGGINGS.get());
        tag(ItemTags.FOOT_ARMOR).add(NVItems.SENTIENT_BOOTS.get());

        tag(ItemTags.SWORDS).add(NVItems.SENTIENT_SWORD.get(), NVItems.LEX_VITAE.get());
        tag(ItemTags.AXES).add(NVItems.SENTIENT_AXE.get(), NVItems.LEX_VITAE.get());
        tag(ItemTags.PICKAXES).add(NVItems.SENTIENT_PICKAXE.get(), NVItems.LEX_VITAE.get());
        tag(ItemTags.SHOVELS).add(NVItems.SENTIENT_SHOVEL.get(), NVItems.LEX_VITAE.get());
        tag(ItemTags.HOES).add(NVItems.LEX_VITAE.get());
        tag(ItemTags.MINING_ENCHANTABLE).add(NVItems.SENTIENT_PICKAXE.get(), NVItems.SENTIENT_SHOVEL.get(), NVItems.SENTIENT_AXE.get(), NVItems.LEX_VITAE.get());
        tag(ItemTags.MINING_LOOT_ENCHANTABLE).add(NVItems.SENTIENT_PICKAXE.get(), NVItems.SENTIENT_SHOVEL.get(), NVItems.SENTIENT_AXE.get(), NVItems.LEX_VITAE.get());
        // ItemTags.SWORD_ENCHANTABLE removed in 26.1 — MELEE_WEAPON_ENCHANTABLE + SHARP_WEAPON_ENCHANTABLE cover the use case.
        tag(ItemTags.MELEE_WEAPON_ENCHANTABLE).add(NVItems.SENTIENT_SWORD.get(), NVItems.LEX_VITAE.get());
        tag(ItemTags.SHARP_WEAPON_ENCHANTABLE).add(NVItems.SENTIENT_SWORD.get(), NVItems.SENTIENT_AXE.get(), NVItems.LEX_VITAE.get());
        tag(ItemTags.WEAPON_ENCHANTABLE).add(NVItems.SENTIENT_SWORD.get(), NVItems.SENTIENT_AXE.get(), NVItems.LEX_VITAE.get());
        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).add(NVItems.SENTIENT_SWORD.get(), NVItems.LEX_VITAE.get());
        tag(ItemTags.DURABILITY_ENCHANTABLE)
                .add(NVItems.SENTIENT_SWORD.get(), NVItems.SENTIENT_AXE.get(),
                        NVItems.SENTIENT_PICKAXE.get(), NVItems.SENTIENT_SHOVEL.get(),
                        NVItems.SENTIENT_SCYTHE.get(), NVItems.LEX_VITAE.get());
        tag(ItemTags.VANISHING_ENCHANTABLE)
                .add(NVItems.SENTIENT_SWORD.get(), NVItems.SENTIENT_AXE.get(),
                        NVItems.SENTIENT_PICKAXE.get(), NVItems.SENTIENT_SHOVEL.get(),
                        NVItems.SENTIENT_SCYTHE.get(), NVItems.LEX_VITAE.get());

        tag(NVTags.Items.SPIRITUS_CRYSTALS)
                .add(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get())
                .add(NVItems.SPIRITUS_RUINA_CRYSTAL_ITEM.get())
                .add(NVItems.SPIRITUS_NIHILUM_CRYSTAL_ITEM.get())
                .add(NVItems.SPIRITUS_INVICTUS_CRYSTAL_ITEM.get())
                .add(NVItems.SPIRITUS_VINDICTA_CRYSTAL_ITEM.get());

        tag(NVTags.Items.REVERTER)
                .add(NVItems.SANGUINE_REVERTER.get());

        tag(NVTags.Items.EXPLOSIVES)
                .add(NVItems.EXPLOSIVE_POWDER.get())
                .add(NVItems.PRIMITIVE_EXPLOSIVE_CELL.get())
                .add(NVItems.HELLFORGED_EXPLOSIVE_CELL.get());

        tag(NVTags.Items.RESONATOR)
                .add(NVItems.RESONATOR.get())
                .add(NVItems.PRIMITIVE_CRYSTALLINE_RESONATOR.get())
                .add(NVItems.HELLFORGED_RESONATOR.get());

        tag(NVTags.Items.CUTTING_FLUIDS)
                .add(NVItems.BASIC_CUTTING_FLUID.get())
                .add(NVItems.INTERMEDIATE_CUTTING_FLUID.get())
                .add(NVItems.ADVANCED_CUTTING_FLUID.get());

        tag(NVTags.Items.HYDRATION)
                .add(NVItems.PRIMITIVE_HYDRATION_CELL.get());

        tag(NVTags.Items.ARC_BLASTING);
        tag(NVTags.Items.ARC_SMELTING)
                .add(NVItems.PRIMITIVE_FURNACE_CELL.get())
                .add(NVItems.LAVA_CRYSTAL.get());
        tag(NVTags.Items.ARC_SMOKING);

        tag(NVTags.Items.ATHANOR_FURNACE)
                .addTag(NVTags.Items.ARC_BLASTING)
                .addTag(NVTags.Items.ARC_SMELTING)
                .addTag(NVTags.Items.ARC_SMOKING);

        tag(NVTags.Items.ATHANOR_TOOL)
                .addTag(NVTags.Items.REVERTER)
                .addTag(NVTags.Items.EXPLOSIVES)
                .addTag(NVTags.Items.RESONATOR)
                .addTag(NVTags.Items.CUTTING_FLUIDS)
                .addTag(NVTags.Items.HYDRATION)
                .addTag(NVTags.Items.ATHANOR_FURNACE)
                .addTag(NVTags.Items.LINGERING_FLASK);

        tag(NVTags.Items.LINGERING_FLASK)
                .add(NVItems.ALCHEMY_FLASK_LINGERING.get());

        tag(NVTags.Items.DUSTS_SULFUR).add(NVItems.SULFUR.get());
        tag(NVTags.Items.DUSTS_SALTPETER).add(NVItems.SALTPETER.get());
        tag(NVTags.Items.DUSTS_CORRUPTED).add(NVItems.CORRUPTED_DUST.get());
        tag(NVTags.Items.TINY_DUSTS_CORRUPTED).add(NVItems.CORRUPTED_DUST_TINY.get());

        // Ingot tags
        tag(NVTags.Items.INGOTS_HELLFORGED).add(NVItems.HELLFORGED_INGOT.get());

        // Raw material tags
        tag(NVTags.Items.RAW_MATERIALS_HELLFORGED).add(NVItems.DEMONITE_RAW.get());

        // Armor trim material
        tag(ItemTags.TRIM_MATERIALS).add(NVItems.DEMONITE_TRIM_INGOT.get());

        tag(NVTags.Items.ANOINTABLE_MELEE)
                .addTag(ItemTags.SWORDS)
                .addTag(ItemTags.AXES);

        tag(NVTags.Items.ANOINTABLE_MINING)
                .addTag(ItemTags.PICKAXES)
                .addTag(ItemTags.SHOVELS)
                .addTag(ItemTags.AXES);

        tag(NVTags.Items.ANOINTABLE_BOWS)
                .add(Items.BOW)
                .add(Items.CROSSBOW);

        tag(NVTags.Items.ANOINTABLE_WEAPONS)
                .addTag(NVTags.Items.ANOINTABLE_MELEE)
                .addTag(NVTags.Items.ANOINTABLE_BOWS);
    }

    private void addBlockItems(TagKey<Item> itemTag, List<Block> blocks) {
        var appender = tag(itemTag);
        for (Block block : blocks) {
            appender.add(block.asItem());
        }
    }
}
