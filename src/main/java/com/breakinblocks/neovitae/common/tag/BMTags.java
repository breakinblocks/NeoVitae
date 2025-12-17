package com.breakinblocks.neovitae.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.BMMaterialsAndTiers;
import com.breakinblocks.neovitae.common.living.LivingUpgrade;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.registry.BMRegistries;

public class BMTags {
    public static class Items {
        public static final TagKey<Item> SOUL_GEM = tag(bm("soul_gems"));
        public static final TagKey<Item> DEMON_CRYSTALS = tag(bm("crystals/demon"));

        public static final TagKey<Item> STORAGE_BLOCKS_HELLFORGED = fromBlock(Blocks.STORAGE_BLOCKS_HELLFORGED);

        public static final TagKey<Item> ARC_TOOL = tag(bm("arc_tool"));

        public static final TagKey<Item> REVERTER = withParent(ARC_TOOL, bm("reverter"));
        public static final TagKey<Item> RESONATOR = withParent(ARC_TOOL, bm("resonator"));
        public static final TagKey<Item> EXPLOSIVES = withParent(ARC_TOOL, bm("explosives"));
        public static final TagKey<Item> CUTTING_FLUIDS = withParent(ARC_TOOL, bm("cutting_fluids"));
        public static final TagKey<Item> HYDRATION = withParent(ARC_TOOL, bm("hydration"));

        public static final TagKey<Item> ARC_FURNACE = withParent(ARC_TOOL, bm("furnace"));
        public static final TagKey<Item> ARC_BLASTING = withParent(ARC_FURNACE, bm("blasting"));
        public static final TagKey<Item> ARC_SMELTING = withParent(ARC_FURNACE, bm("smelting"));
        public static final TagKey<Item> ARC_SMOKING = withParent(ARC_FURNACE, bm("smoking"));

        public static final TagKey<Item> LINGERING_FLASK = withParent(ARC_TOOL, bm("lingering_flask"));

        // Explosive charges - excluded from loot modifier processing
        public static final TagKey<Item> CHARGES = tag(bm("charges"));

        public static final TagKey<Item> LIVING_UPGRADE_SET = tag(bm("living_upgrade_set"));
        public static final TagKey<Item> LIVING_SET = withParent(LIVING_UPGRADE_SET, BMMaterialsAndTiers.LIVING_ARMOUR_MATERIAL.getId());

        // Processing item tags - use c: convention for cross-mod compatibility
        public static final TagKey<Item> FRAGMENTS_IRON = tag(c("fragments/iron"));
        public static final TagKey<Item> FRAGMENTS_GOLD = tag(c("fragments/gold"));
        public static final TagKey<Item> FRAGMENTS_COPPER = tag(c("fragments/copper"));
        public static final TagKey<Item> FRAGMENTS_NETHERITE_SCRAP = tag(c("fragments/netherite_scrap"));
        public static final TagKey<Item> FRAGMENTS_HELLFORGED = tag(c("fragments/hellforged"));

        public static final TagKey<Item> GRAVELS_IRON = tag(c("gravels/iron"));
        public static final TagKey<Item> GRAVELS_GOLD = tag(c("gravels/gold"));
        public static final TagKey<Item> GRAVELS_COPPER = tag(c("gravels/copper"));
        public static final TagKey<Item> GRAVELS_NETHERITE_SCRAP = tag(c("gravels/netherite_scrap"));
        public static final TagKey<Item> GRAVELS_HELLFORGED = tag(c("gravels/hellforged"));

        // Dust tags (sands)
        public static final TagKey<Item> DUSTS_IRON = tag(c("dusts/iron"));
        public static final TagKey<Item> DUSTS_GOLD = tag(c("dusts/gold"));
        public static final TagKey<Item> DUSTS_COPPER = tag(c("dusts/copper"));
        public static final TagKey<Item> DUSTS_COAL = tag(c("dusts/coal"));
        public static final TagKey<Item> DUSTS_SULFUR = tag(c("dusts/sulfur"));
        public static final TagKey<Item> DUSTS_SALTPETER = tag(c("dusts/saltpeter"));
        public static final TagKey<Item> DUSTS_NETHERITE_SCRAP = tag(c("dusts/netherite_scrap"));
        public static final TagKey<Item> DUSTS_HELLFORGED = tag(c("dusts/hellforged"));
        public static final TagKey<Item> DUSTS_CORRUPTED = tag(c("dusts/corrupted"));
        public static final TagKey<Item> TINY_DUSTS_CORRUPTED = tag(c("tiny_dusts/corrupted"));

        // Ingot tags
        public static final TagKey<Item> INGOTS_HELLFORGED = tag(c("ingots/hellforged"));
        // Raw material tags
        public static final TagKey<Item> RAW_MATERIALS_HELLFORGED = tag(c("raw_materials/hellforged"));

        private static TagKey<Item> fromBlock(TagKey<Block> input) {
            return tag(input.location());
        }

        private static TagKey<Item> withParent(TagKey<Item> parent, ResourceLocation location) {
            return TagKey.create(Registries.ITEM, location.withPrefix(parent.location().getPath()+"/"));
        }

        private static TagKey<Item> tag(ResourceLocation id) {
            return TagKey.create(Registries.ITEM, id);
        }
    }

    public static class Blocks {
        public static final TagKey<Block> RUNES = tag(bm("altar/runes"));
        public static final TagKey<Block> PILLARS = tag(bm("altar/pillars"));
        public static final TagKey<Block> T3_CAPSTONES = tag(bm("altar/t3_capstones"));
        public static final TagKey<Block> T4_CAPSTONES = tag(bm("altar/t4_capstones"));
        public static final TagKey<Block> T5_CAPSTONES = tag(bm("altar/t5_capstones"));
        public static final TagKey<Block> T6_CAPSTONES = tag(bm("altar/t6_capstones"));

        public static final TagKey<Block> PULSE_ON_CRAFTING = tag(bm("altar/pulse_on_crafting"));
        public static final TagKey<Block> SOUL_NETWORK_COMPARATOR = tag(bm("altar/soul_network_comparator"));

        public static final TagKey<Block> STORAGE_BLOCKS_HELLFORGED = tag(c("storage_blocks/hellforged"));

        // Incense Altar Path Blocks - blocks in higher level tags also work for lower levels
        // Level 0 = innermost ring (distance 2), Level 10 = outermost ring (distance 12)
        public static final TagKey<Block> INCENSE_PATH_LEVEL_0 = tag(bm("incense_path/level_0"));
        public static final TagKey<Block> INCENSE_PATH_LEVEL_1 = tag(bm("incense_path/level_1"));
        public static final TagKey<Block> INCENSE_PATH_LEVEL_2 = tag(bm("incense_path/level_2"));
        public static final TagKey<Block> INCENSE_PATH_LEVEL_3 = tag(bm("incense_path/level_3"));
        public static final TagKey<Block> INCENSE_PATH_LEVEL_4 = tag(bm("incense_path/level_4"));
        public static final TagKey<Block> INCENSE_PATH_LEVEL_5 = tag(bm("incense_path/level_5"));
        public static final TagKey<Block> INCENSE_PATH_LEVEL_6 = tag(bm("incense_path/level_6"));
        public static final TagKey<Block> INCENSE_PATH_LEVEL_7 = tag(bm("incense_path/level_7"));
        public static final TagKey<Block> INCENSE_PATH_LEVEL_8 = tag(bm("incense_path/level_8"));
        public static final TagKey<Block> INCENSE_PATH_LEVEL_9 = tag(bm("incense_path/level_9"));
        public static final TagKey<Block> INCENSE_PATH_LEVEL_10 = tag(bm("incense_path/level_10"));

        // Tranquility blocks - blocks that provide tranquility near an incense altar
        public static final TagKey<Block> TRANQUILITY_PLANT = tag(bm("tranquility/plant"));
        public static final TagKey<Block> TRANQUILITY_CROP = tag(bm("tranquility/crop"));
        public static final TagKey<Block> TRANQUILITY_TREE = tag(bm("tranquility/tree"));
        public static final TagKey<Block> TRANQUILITY_EARTHEN = tag(bm("tranquility/earthen"));
        public static final TagKey<Block> TRANQUILITY_WATER = tag(bm("tranquility/water"));
        public static final TagKey<Block> TRANQUILITY_FIRE = tag(bm("tranquility/fire"));
        public static final TagKey<Block> TRANQUILITY_LAVA = tag(bm("tranquility/lava"));

        // Fungal charge blocks - mushroom blocks to harvest
        public static final TagKey<Block> MUSHROOM_HYPHAE = tag(bm("mushroom_hyphae"));
        public static final TagKey<Block> MUSHROOM_STEM = tag(bm("mushroom_stem"));

        // Mundane blocks - drops are voided by voiding anointment
        public static final TagKey<Block> MUNDANE_BLOCK = tag(bm("mundane_block"));

        private static TagKey<Block> tag(ResourceLocation id) {
            return TagKey.create(Registries.BLOCK, id);
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> LIFE_ESSENCE = tag(bm("life_essence"));

        private static TagKey<Fluid> tag(ResourceLocation id) {
            return TagKey.create(Registries.FLUID, id);
        }
    }

    public static class DamageTypes {
        public static final TagKey<DamageType> SELF_SACRIFICE = TagKey.create(Registries.DAMAGE_TYPE, bm("self_sacrifice"));
        public static final TagKey<DamageType> TOUGH_IGNORED = TagKey.create(Registries.DAMAGE_TYPE, bm("tough_ignored"));
    }

    public static class Entities {
        public static final TagKey<EntityType<?>> TELEPOSE_BLACKLIST = tag(bm("telepose_blacklist"));

        private static TagKey<EntityType<?>> tag(ResourceLocation id) {
            return TagKey.create(Registries.ENTITY_TYPE, id);
        }
    }

    public static class Tiers {
        public static final TagKey<AltarTier> VALID_TIERS = TagKey.create(BMRegistries.Keys.ALTAR_TIER_KEY, bm("valid_tiers"));
    }

    public static class Living {
        public static final TagKey<LivingUpgrade> TOOLTIP_ORDER = tag(bm("tooltip_order"));
        public static final TagKey<LivingUpgrade> TOOLTIP_HIDE = tag(bm("tooltip_hide"));
        public static final TagKey<LivingUpgrade> IS_DOWNGRADE = tag(bm("is_downgrade"));
        public static final TagKey<LivingUpgrade> LIVING_START = tag(bm("living_start"));
        public static final TagKey<LivingUpgrade> TRAINERS = tag(bm("trainer"));
        public static final TagKey<LivingUpgrade> IS_SCRAPPABLE = tag(bm("is_scrappable"));
        /** Upgrades in this tag are unsuitable for Living Armor and should not be applied */
        public static final TagKey<LivingUpgrade> LIVING_BLACKLIST = tag(bm("living_blacklist"));

        private static TagKey<LivingUpgrade> tag(ResourceLocation id) {
            return TagKey.create(BMRegistries.Keys.LIVING_UPGRADES, id);
        }
    }

    private static ResourceLocation bm(String path) {
        return ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, path);
    }

    private static ResourceLocation c(String path) {
        return ResourceLocation.fromNamespaceAndPath("c", path);
    }
}
