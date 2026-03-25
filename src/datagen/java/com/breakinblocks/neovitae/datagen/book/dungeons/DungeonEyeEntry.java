package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class DungeonEyeEntry extends EntryProvider {

    public DungeonEyeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_eye"))
                .withText(this.context().pageText()));
        this.pageText("A **Dungeon Eye** is a light-emitting block often found within the depths of the **Endless Realm**.");

        this.page("recipe_c_d", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_eye_c"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_eye_d")));

        this.page("recipe_st_v", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_eye_st"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_eye_v")));
    }

    @Override
    protected String entryName() {
        return "Dungeon Eyes";
    }

    @Override
    protected String entryDescription() {
        return "Light-emitting blocks from the Demon Realm.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(DungeonBlocks.DUNGEON_EYE.get(DungeonVariant.RAW).asItem());
    }

    @Override
    protected String entryId() {
        return "dungeon_eye";
    }
}
