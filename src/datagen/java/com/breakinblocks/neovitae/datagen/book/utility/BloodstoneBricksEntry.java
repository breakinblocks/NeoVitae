package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class BloodstoneBricksEntry extends EntryProvider {

    public BloodstoneBricksEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Bloodstone Bricks");
        this.pageText("[#](8B0000)Bloodstone Bricks[#]() are a decorative block, and used as the capstones for the "
                + "Tier-4 Ara Vitae. They require Weak Blood Shards, which can be made by putting a "
                + "Saturated Tau into an Alchemical Reaction Chamber with a Sanguine Reverter in it.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "bloodstone_brick")));
    }

    @Override
    protected String entryName() {
        return "Bloodstone Bricks";
    }

    @Override
    protected String entryDescription() {
        return "Decorative blocks and Tier-4 altar capstones.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.BLOODSTONE_BRICK.asItem());
    }

    @Override
    protected String entryId() {
        return "bloodstone_bricks";
    }
}
