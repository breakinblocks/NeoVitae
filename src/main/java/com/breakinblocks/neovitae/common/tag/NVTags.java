package com.breakinblocks.neovitae.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVMaterialsAndTiers;
import com.breakinblocks.neovitae.common.sentient.SentientUpgrade;
import com.breakinblocks.neovitae.common.registry.AltarTier;
import com.breakinblocks.neovitae.common.registry.NVRegistries;

public class NVTags {
    public static class Items {
        public static final TagKey<Item> BLOOD_MENDING_BLACKLIST = tag(bm("blood_mending_blacklist"));
        public static final TagKey<Item> SPIRITUS_CAPABLE = tag(bm("spiritus_capable"));
        public static final TagKey<Item> SPIRITUS_GEM = tag(bm("spiritus_gems"));
        public static final TagKey<Item> SPIRITUS_CRYSTALS = tag(bm("crystals/demon"));
        public static final TagKey<Item> SENTIENT_ARMOR_REPAIR = tag(bm("sentient_armor_repair"));

        public static final TagKey<Item> STORAGE_BLOCKS_HELLFORGED = fromBlock(Blocks.STORAGE_BLOCKS_HELLFORGED);

        public static final TagKey<Item> ATHANOR_TOOL = tag(bm("athanor_tool"));

        public static final TagKey<Item> REVERTER = withParent(ATHANOR_TOOL, bm("reverter"));
        public static final TagKey<Item> RESONATOR = withParent(ATHANOR_TOOL, bm("resonator"));
        public static final TagKey<Item> EXPLOSIVES = withParent(ATHANOR_TOOL, bm("explosives"));
        public static final TagKey<Item> CUTTING_FLUIDS = withParent(ATHANOR_TOOL, bm("cutting_fluids"));
        public static final TagKey<Item> HYDRATION = withParent(ATHANOR_TOOL, bm("hydration"));

        public static final TagKey<Item> ATHANOR_FURNACE = withParent(ATHANOR_TOOL, bm("furnace"));
        public static final TagKey<Item> ARC_BLASTING = withParent(ATHANOR_FURNACE, bm("blasting"));
        public static final TagKey<Item> ARC_SMELTING = withParent(ATHANOR_FURNACE, bm("smelting"));
        public static final TagKey<Item> ARC_SMOKING = withParent(ATHANOR_FURNACE, bm("smoking"));

        public static final TagKey<Item> LINGERING_FLASK = withParent(ATHANOR_TOOL, bm("lingering_flask"));

        public static final TagKey<Item> CHARGES = tag(bm("charges"));

        public static final TagKey<Item> SENTIENT_UPGRADE_SET = tag(bm("sentient_upgrade_set"));
        public static final TagKey<Item> SENTIENT_SET = withParent(SENTIENT_UPGRADE_SET, NVMaterialsAndTiers.SENTIENT_EQUIPMENT_ASSET.identifier());

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

        public static final TagKey<Item> INGOTS_HELLFORGED = tag(c("ingots/hellforged"));
        public static final TagKey<Item> RAW_MATERIALS_HELLFORGED = tag(c("raw_materials/hellforged"));

        public static final TagKey<Item> ANOINTABLE_MELEE = tag(bm("anointable/melee"));
        public static final TagKey<Item> ANOINTABLE_MINING = tag(bm("anointable/mining"));
        public static final TagKey<Item> ANOINTABLE_BOWS = tag(bm("anointable/bows"));
        public static final TagKey<Item> ANOINTABLE_WEAPONS = tag(bm("anointable/weapons"));

        private static TagKey<Item> fromBlock(TagKey<Block> input) {
            return tag(input.location());
        }

        private static TagKey<Item> withParent(TagKey<Item> parent, Identifier location) {
            return TagKey.create(Registries.ITEM, location.withPrefix(parent.location().getPath()+"/"));
        }

        private static TagKey<Item> tag(Identifier id) {
            return TagKey.create(Registries.ITEM, id);
        }
    }

    public static class Blocks {
        public static final TagKey<Block> RUNES = tag(bm("altar/runes"));
        public static final TagKey<Block> BLOODSTONES = tag(bm("altar/bloodstones"));
        public static final TagKey<Block> PILLARS = tag(bm("altar/pillars"));
        public static final TagKey<Block> T3_CAPSTONES = tag(bm("altar/t3_capstones"));
        public static final TagKey<Block> T4_CAPSTONES = tag(bm("altar/t4_capstones"));
        public static final TagKey<Block> T5_CAPSTONES = tag(bm("altar/t5_capstones"));
        public static final TagKey<Block> T6_CAPSTONES = tag(bm("altar/t6_capstones"));

        public static final TagKey<Block> PULSE_ON_CRAFTING = tag(bm("altar/pulse_on_crafting"));
        public static final TagKey<Block> ANIMA_COMPARATOR = tag(bm("altar/anima_comparator"));

        public static final TagKey<Block> STORAGE_BLOCKS_HELLFORGED = tag(c("storage_blocks/hellforged"));

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

        public static final TagKey<Block> TRANQUILITY_PLANT = tag(bm("tranquility/plant"));
        public static final TagKey<Block> TRANQUILITY_CROP = tag(bm("tranquility/crop"));
        public static final TagKey<Block> TRANQUILITY_TREE = tag(bm("tranquility/tree"));
        public static final TagKey<Block> TRANQUILITY_EARTHEN = tag(bm("tranquility/earthen"));
        public static final TagKey<Block> TRANQUILITY_WATER = tag(bm("tranquility/water"));
        public static final TagKey<Block> TRANQUILITY_FIRE = tag(bm("tranquility/fire"));
        public static final TagKey<Block> TRANQUILITY_LAVA = tag(bm("tranquility/lava"));

        public static final TagKey<Block> MUSHROOM_HYPHAE = tag(bm("mushroom_hyphae"));
        public static final TagKey<Block> MUSHROOM_STEM = tag(bm("mushroom_stem"));

        public static final TagKey<Block> GEODE_HARVESTABLE = tag(bm("geode_harvestable"));

        public static final TagKey<Block> MUNDANE_BLOCK = tag(bm("mundane_block"));

        private static TagKey<Block> tag(Identifier id) {
            return TagKey.create(Registries.BLOCK, id);
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> ESSENTIA_VITAE = tag(bm("essentia_vitae"));
        public static final TagKey<Fluid> ESSENTIA_VITAE_SOURCE = tag(bm("essentia_vitae_source"));
        public static final TagKey<Fluid> ESSENTIA_VITAE_FLOWING = tag(bm("essentia_vitae_flowing"));
        public static final TagKey<Fluid> ANIMATED_SPIRITUS = tag(bm("animated_spiritus"));
        public static final TagKey<Fluid> ANIMATED_SPIRITUS_SOURCE = tag(bm("animated_spiritus_source"));
        public static final TagKey<Fluid> ANIMATED_SPIRITUS_FLOWING = tag(bm("animated_spiritus_flowing"));

        private static TagKey<Fluid> tag(Identifier id) {
            return TagKey.create(Registries.FLUID, id);
        }
    }

    public static class DamageTypes {
        public static final TagKey<DamageType> SELF_SACRIFICE = TagKey.create(Registries.DAMAGE_TYPE, bm("self_sacrifice"));
        public static final TagKey<DamageType> TOUGH_IGNORED = TagKey.create(Registries.DAMAGE_TYPE, bm("tough_ignored"));
    }

    public static class Entities {
        public static final TagKey<EntityType<?>> TELEPOSE_BLACKLIST = tag(bm("telepose_blacklist"));
        public static final TagKey<EntityType<?>> WELL_OF_SUFFERING_BLACKLIST = tag(bm("well_of_suffering_blacklist"));
        public static final TagKey<EntityType<?>> RITUAL_BOSS_BLACKLIST = tag(bm("ritual_boss_blacklist"));
        public static final TagKey<EntityType<?>> NO_SACRIFICE = tag(bm("no_sacrifice"));

        private static TagKey<EntityType<?>> tag(Identifier id) {
            return TagKey.create(Registries.ENTITY_TYPE, id);
        }
    }

    public static class Tiers {
        public static final TagKey<AltarTier> VALID_TIERS = TagKey.create(NVRegistries.Keys.ALTAR_TIER_KEY, bm("valid_tiers"));
    }

    public static class Sentient {
        public static final TagKey<SentientUpgrade> TOOLTIP_ORDER = tag(bm("tooltip_order"));
        public static final TagKey<SentientUpgrade> TOOLTIP_HIDE = tag(bm("tooltip_hide"));
        public static final TagKey<SentientUpgrade> IS_DOWNGRADE = tag(bm("is_downgrade"));
        public static final TagKey<SentientUpgrade> SENTIENT_START = tag(bm("sentient_start"));
        public static final TagKey<SentientUpgrade> TRAINERS = tag(bm("trainer"));
        public static final TagKey<SentientUpgrade> IS_SCRAPPABLE = tag(bm("is_scrappable"));
        public static final TagKey<SentientUpgrade> SENTIENT_BLACKLIST = tag(bm("sentient_blacklist"));

        private static TagKey<SentientUpgrade> tag(Identifier id) {
            return TagKey.create(NVRegistries.Keys.SENTIENT_UPGRADES, id);
        }
    }

    private static Identifier bm(String path) {
        return Identifier.fromNamespaceAndPath(NeoVitae.MODID, path);
    }

    private static Identifier c(String path) {
        return Identifier.fromNamespaceAndPath("c", path);
    }
}
