package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.tag.BMTags;

import java.util.List;

public class BMMaterialsAndTiers {
    public static final DeferredRegister<ArmorMaterial> ARMOUR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, NeoVitae.MODID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> LIVING_ARMOUR_MATERIAL = ARMOUR_MATERIALS.register("living", () -> new ArmorMaterial(
            ArmorMaterials.IRON.value().defense(), ArmorMaterials.IRON.value().enchantmentValue(),
            ArmorMaterials.IRON.value().equipSound(), () -> Ingredient.of(BMItems.RAW_WILL.get()),
            List.of(new ArmorMaterial.Layer(bm("living"))), 0, 0
    ));

    // Sentient tool tier (demon crystal repaired)
    public static final Tier SENTIENT = new Tier() {
        @Override
        public int getUses() {
            return 520;
        }

        @Override
        public float getSpeed() {
            return 6.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 2.0F;
        }

        @Override
        public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        }

        @Override
        public int getEnchantmentValue() {
            return 50;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(BMTags.Items.DEMON_CRYSTALS);
        }
    };

    private static ResourceLocation bm(String path) {
        return ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, path);
    }

    public static void register(IEventBus modBus) {
        ARMOUR_MATERIALS.register(modBus);
    }
}
