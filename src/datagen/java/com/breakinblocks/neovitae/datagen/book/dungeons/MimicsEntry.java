package com.breakinblocks.neovitae.datagen.book.dungeons;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class MimicsEntry extends EntryProvider {

    public MimicsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ethereal Mimics");
        this.pageText("[#](8B0000)Ethereal Mimics[#]() are a block that can easily take on the appearance of any other, while "
                + "being completely intangible. These can make them quite handy for concealing pitfall traps and secret "
                + "entrances. Keep your eye out and you may find these in the [#](8B0000)Endless Realm[#]().");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ethereal_mimic"))
                .withText(this.context().pageText()));
        this.pageText("Simply right-click the placed mimic with any other block to encourage it to take on its form.");
    }

    @Override
    protected String entryName() {
        return "Ethereal Mimics";
    }

    @Override
    protected String entryDescription() {
        return "Shape-shifting intangible blocks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.ETHEREAL_MIMIC.asItem());
    }

    @Override
    protected String entryId() {
        return "mimics";
    }
}
