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
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class CapacityRuneEntry extends EntryProvider {

    public CapacityRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Rune of Capacity");
        this.pageText("The [#](8B0000)Rune of Capacity[#]() deepens the altar's basin, allowing the [#](8B0000)Ara Vitae[#]() "
                + "to hold more [#](4A0080)Essentia Vitae[#]() before overflowing. Each rune expands the reservoir "
                + "by [#](8B0000)+20%%[#](), stacking additively. A deeper well means fewer interruptions during "
                + "costly transmutations.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "rune_capacity")));

    }

    @Override
    protected String entryName() {
        return "Rune of Capacity";
    }

    @Override
    protected String entryDescription() {
        return "Deepens the altar's reservoir to hold more Essentia Vitae.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_CAPACITY.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_capacity";
    }
}
