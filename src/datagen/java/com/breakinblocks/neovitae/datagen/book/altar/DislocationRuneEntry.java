package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import net.minecraft.resources.Identifier;
import com.mojang.datafixers.util.Pair;

public class DislocationRuneEntry extends EntryProvider {

    public DislocationRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Displacement Rune");
        this.pageText("The [#](8B0000)Displacement Rune[#]() widens the unseen channels through which "
                + "[#](4A0080)Essentia Vitae[#]() flows between the altar and external vessels. Each rune "
                + "increases the fluid transfer rate by a multiplicative [#](8B0000)+20%%[#](). Essential for any "
                + "Vitaemancer who stores essence in outside tanks.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "rune_dislocation")));

    }

    @Override
    protected String entryName() {
        return "Displacement Rune";
    }

    @Override
    protected String entryDescription() {
        return "Widens the flow of Essentia Vitae to and from external vessels.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_DISLOCATION.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_dislocation";
    }
}
