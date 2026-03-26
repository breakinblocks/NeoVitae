package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSmeltingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.dungeon.DungeonBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class DemoniteEntry extends EntryProvider {

    public DemoniteEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demonite");
        this.pageText("[#](8B0000)Demonite Ore[#]() can only be found within [#](8B0000)Dungeons[#](), specifically in [#](8B0000)The Mines[#](), "
                + "accessed by finding a [#](8B0000)Foreman's Key[#]() somewhere within the mazelike structure and using it to unlock "
                + "a [#](8B0000)Spatial Distortion[#](). It can be used to make [#](8B0000)Hellforged Ingots[#]() or [#](8B0000)Hellforged Sand[#]().\\\n\\\n"
                + "When mined with Silk Touch, the ore can be harvested directly, but otherwise, it will drop clumps of [#](8B0000)Raw Demonite[#]().");

        this.page("smelting", () -> BookSmeltingRecipePageModel.create()
                .withTitle1("Hellforged Ingot")
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "smelting/ingot_from_demonite"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "smelting/ingot_from_raw_hellforged")));

        this.page("smelting2", () -> BookSmeltingRecipePageModel.create()
                .withTitle1("Hellforged Ingot")
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "smelting/ingot_hellforged")));

        this.page("arc_dust", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Hellforged Sand");
        this.pageText("Hellforged Sand can be produced in the Alchemical Reaction Chamber from gravel, ore, or raw demonite.");

        this.page("arc_processing", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demonite Processing");
        this.pageText("Demonite can also be processed through the Alchemical Reaction Chamber to produce fragments and gravel.");

        this.page("hellforged_block", () -> BookCraftingRecipePageModel.create()
                .withTitle1("Hellforged Block")
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "hellforged_block"))
                .withRecipeId2(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "raw_hellforged_block")));

        this.page("decorative", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Decorative Variants");
        this.pageText("Several decorative variants of this block exist, being faintly tainted with Corrosive, "
                + "Destructive, Steadfast, or Vengeful Will in a [#](8B0000)stonecutter[#]().\\\n\\\n"
                + "They can each be directly crafted back into 9 [#](8B0000)Hellforged Ingots[#]().");
    }

    @Override
    protected String entryName() {
        return "Demonite";
    }

    @Override
    protected String entryDescription() {
        return "Ore found within the Demon Realm mines.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(DungeonBlocks.DUNGEON_ORE.asItem());
    }

    @Override
    protected String entryId() {
        return "demonite";
    }
}
