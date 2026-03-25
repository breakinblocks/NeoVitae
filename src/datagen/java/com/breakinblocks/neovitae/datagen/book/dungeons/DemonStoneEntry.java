package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonVariant;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class DemonStoneEntry extends EntryProvider {

    public DemonStoneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Stone");
        this.pageText("The **Demon Realm** is primarily built of an odd pale-blue material unknown in the rest of reality, "
                + "aptly dubbed \"Demon Stone\" for what one would hope are self-evident reasons. Its construction seems quite "
                + "simple, being an infusion of stone with a small amount of Will.");

        this.page("recipe_stone", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_stone")));

        this.page("recipe_spread", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_stone_spread"))
                .withText(this.context().pageText()));
        this.pageText("This stone appears uniquely aggressive to material from our world; any attempt to mix "
                + "Demon Stone and regular stone simply results in more Demon Stone.");

        this.page("recipe_variants_1", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_stone_c"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_stone_d")));

        this.page("recipe_variants_2", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_stone_st"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_stone_v")));

        this.page("variants_info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Coloured Variants");
        this.pageText("Various coloured variants of this stone can be created by introducing stone to Corrosive, "
                + "Destructive, Steadfast, or Vengeful Will, as one may expect. As with Raw will, they can be crafted "
                + "in a stone cutter to all varieties of pattern.");

        this.page("recipe_spread_c_d", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_stone_c_spread"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_stone_d_spread")));

        this.page("recipe_spread_s_v", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_stone_s_spread"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "dungeon_stone_v_spread")));
    }

    @Override
    protected String entryName() {
        return "Demon Stone";
    }

    @Override
    protected String entryDescription() {
        return "The building blocks of the Demon Realm.";
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
        return "demon_stone";
    }
}
