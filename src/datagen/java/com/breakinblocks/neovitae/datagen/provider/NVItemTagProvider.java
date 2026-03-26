package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.common.tag.NVTags;

import java.util.concurrent.CompletableFuture;

public class NVItemTagProvider extends ItemTagsProvider {
    public NVItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, NeoVitae.MODID, null);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        copy(NVTags.Blocks.STORAGE_BLOCKS_HELLFORGED, NVTags.Items.STORAGE_BLOCKS_HELLFORGED);

        tag(NVTags.Items.LIVING_SET)
                .add(NVItems.LIVING_HELMET.get(), NVItems.LIVING_PLATE.get(), NVItems.LIVING_LEGGINGS.get(), NVItems.LIVING_BOOTS.get());

        tag(NVTags.Items.LIVING_UPGRADE_SET)
                .addTag(NVTags.Items.LIVING_SET);

        // Add living armor to vanilla armor tags for mod compatibility
        tag(ItemTags.HEAD_ARMOR).add(NVItems.LIVING_HELMET.get());
        tag(ItemTags.CHEST_ARMOR).add(NVItems.LIVING_PLATE.get());
        tag(ItemTags.LEG_ARMOR).add(NVItems.LIVING_LEGGINGS.get());
        tag(ItemTags.FOOT_ARMOR).add(NVItems.LIVING_BOOTS.get());

        tag(NVTags.Items.SPIRITUS_CRYSTALS)
                .add(NVItems.RAW_CRYSTAL.get())
                .add(NVItems.CORROSIVE_CRYSTAL.get())
                .add(NVItems.DESTRUCTIVE_CRYSTAL.get())
                .add(NVItems.STEADFAST_CRYSTAL.get())
                .add(NVItems.VENGEFUL_CRYSTAL.get());

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
                .add(NVItems.BASIC_CUTTING_FLUID.get());

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

        // Processing item tags
        tag(NVTags.Items.FRAGMENTS_IRON).add(NVItems.IRON_FRAGMENT.get());
        tag(NVTags.Items.FRAGMENTS_GOLD).add(NVItems.GOLD_FRAGMENT.get());
        tag(NVTags.Items.FRAGMENTS_COPPER).add(NVItems.COPPER_FRAGMENT.get());
        tag(NVTags.Items.FRAGMENTS_NETHERITE_SCRAP).add(NVItems.NETHERITE_SCRAP_FRAGMENT.get());
        tag(NVTags.Items.FRAGMENTS_HELLFORGED).add(NVItems.DEMONITE_FRAGMENT.get());

        tag(NVTags.Items.GRAVELS_IRON).add(NVItems.IRON_GRAVEL.get());
        tag(NVTags.Items.GRAVELS_GOLD).add(NVItems.GOLD_GRAVEL.get());
        tag(NVTags.Items.GRAVELS_COPPER).add(NVItems.COPPER_GRAVEL.get());
        tag(NVTags.Items.GRAVELS_NETHERITE_SCRAP).add(NVItems.NETHERITE_SCRAP_GRAVEL.get());
        tag(NVTags.Items.GRAVELS_HELLFORGED).add(NVItems.DEMONITE_GRAVEL.get());

        // Dust tags (sands)
        tag(NVTags.Items.DUSTS_IRON).add(NVItems.IRON_SAND.get());
        tag(NVTags.Items.DUSTS_GOLD).add(NVItems.GOLD_SAND.get());
        tag(NVTags.Items.DUSTS_COPPER).add(NVItems.COPPER_SAND.get());
        tag(NVTags.Items.DUSTS_COAL).add(NVItems.COAL_SAND.get());
        tag(NVTags.Items.DUSTS_SULFUR).add(NVItems.SULFUR.get());
        tag(NVTags.Items.DUSTS_SALTPETER).add(NVItems.SALTPETER.get());
        tag(NVTags.Items.DUSTS_NETHERITE_SCRAP).add(NVItems.NETHERITE_SCRAP_SAND.get());
        tag(NVTags.Items.DUSTS_HELLFORGED).add(NVItems.HELLFORGED_SAND.get());
        tag(NVTags.Items.DUSTS_CORRUPTED).add(NVItems.CORRUPTED_DUST.get());
        tag(NVTags.Items.TINY_DUSTS_CORRUPTED).add(NVItems.CORRUPTED_DUST_TINY.get());

        // Ingot tags
        tag(NVTags.Items.INGOTS_HELLFORGED).add(NVItems.HELLFORGED_INGOT.get());

        // Raw material tags
        tag(NVTags.Items.RAW_MATERIALS_HELLFORGED).add(NVItems.DEMONITE_RAW.get());
    }
}
