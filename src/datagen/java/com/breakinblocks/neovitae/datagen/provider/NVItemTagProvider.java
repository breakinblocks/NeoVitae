package com.breakinblocks.neovitae.datagen.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
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

        copy(NVTags.Blocks.RUNES, NVTags.Items.RUNES);
        copy(NVTags.Blocks.BLOODSTONES, NVTags.Items.BLOODSTONES);
        copy(NVTags.Blocks.T3_CAPSTONES, NVTags.Items.T3_CAPSTONES);
        copy(NVTags.Blocks.T4_CAPSTONES, NVTags.Items.T4_CAPSTONES);
        copy(NVTags.Blocks.T5_CAPSTONES, NVTags.Items.T5_CAPSTONES);
        copy(NVTags.Blocks.T6_CAPSTONES, NVTags.Items.T6_CAPSTONES);
        copy(NVTags.Blocks.ANIMA_COMPARATOR, NVTags.Items.ANIMA_COMPARATOR);

        tag(NVTags.Items.VITAE_STONE)
                .add(Items.DEEPSLATE);

        tag(NVTags.Items.SPIRITUS_GEM)
                .add(NVItems.SPIRITUS_GEM_PETTY.get())
                .add(NVItems.SPIRITUS_GEM_LESSER.get())
                .add(NVItems.SPIRITUS_GEM_COMMON.get())
                .add(NVItems.SPIRITUS_GEM_GREATER.get())
                .add(NVItems.SPIRITUS_GEM_GRAND.get());

        tag(NVTags.Items.SENTIENT_SET)
                .add(NVItems.SENTIENT_HELMET.get(), NVItems.SENTIENT_PLATE.get(), NVItems.SENTIENT_LEGGINGS.get(), NVItems.SENTIENT_BOOTS.get());

        tag(NVTags.Items.SENTIENT_UPGRADE_SET)
                .addTag(NVTags.Items.SENTIENT_SET);

        // Add sentient armor to vanilla armor tags for mod compatibility
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
        tag(ItemTags.SWORD_ENCHANTABLE).add(NVItems.SENTIENT_SWORD.get(), NVItems.LEX_VITAE.get());
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
}
