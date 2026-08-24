package com.breakinblocks.neovitae.compat.emi;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.item.NVItems;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;

public final class NVEmiCategories {

    public static final EmiRecipeCategory ARA_VITAE = category("ara_vitae", NVBlocks.ARA_VITAE.block().get());
    public static final EmiRecipeCategory HELLFIRE_FORGE = category("hellfire_forge", NVBlocks.HELLFIRE_FORGE.block().get());
    public static final EmiRecipeCategory FORGE_UPGRADE = category("forge_upgrade", NVBlocks.HELLFIRE_FORGE.block().get());
    public static final EmiRecipeCategory ALCHEMY_ARRAY_CRAFTING = category("alchemy_array", NVItems.ARCANE_SCRIBE_TOOL.get());
    public static final EmiRecipeCategory ALCHEMY_ARRAY_EFFECT = category("alchemy_array_effect", NVItems.ARCANE_SCRIBE_TOOL.get());
    public static final EmiRecipeCategory TABULA_VITAE = category("tabula_vitae", NVBlocks.TABULA_VITAE.block().get());
    public static final EmiRecipeCategory METEOR = category("meteor", NVItems.RAW_SPIRITUS.get());
    public static final EmiRecipeCategory ATHANOR = category("athanor", NVBlocks.ATHANOR_BLOCK.block().get());
    public static final EmiRecipeCategory FLASK = category("flask", NVItems.ALCHEMY_FLASK.get());
    public static final EmiRecipeCategory FLASK_COMBINATION = category("flask_combination", NVItems.ALCHEMY_FLASK.get());
    public static final EmiRecipeCategory IMPERFECT_RITUAL = category("imperfect_ritual", NVBlocks.IMPERFECT_RITUAL_STONE.block().get());
    public static final EmiRecipeCategory RITUAL = category("ritual", NVBlocks.MASTER_RITUAL_STONE.block().get());
    public static final EmiRecipeCategory BLOOD_TANK_UPGRADE = category("blood_tank_upgrade", NVBlocks.BLOOD_TANK.block().get());
    public static final EmiRecipeCategory DISENCHANT = category("disenchant", NVItems.SANGUINE_REVERTER.get());

    private NVEmiCategories() {
    }

    private static EmiRecipeCategory category(String path, ItemLike icon) {
        EmiRenderable renderable = EmiStack.of(icon == null ? Items.BARRIER : icon);
        return new EmiRecipeCategory(NeoVitae.rl(path), renderable, renderable);
    }

    public static ResourceLocation recipeId(String category, Object key) {
        return synthetic(category + "/" + key);
    }

    public static ResourceLocation synthetic(String path) {
        return NeoVitae.rl("/" + path);
    }

    public static ResourceLocation synthetic(ResourceLocation base) {
        return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), "/" + base.getPath());
    }
}
