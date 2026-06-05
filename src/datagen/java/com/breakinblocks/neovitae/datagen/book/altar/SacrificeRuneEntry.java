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

public class SacrificeRuneEntry extends EntryProvider {

    public SacrificeRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Rune of Sacrifice");
        this.pageText("The [#](8B0000)Rune of Sacrifice[#]() deepens the altar's thirst for the blood of others. "
                + "When a creature is slain near the [#](8B0000)Ara Vitae[#](), "
                + "each rune amplifies the [#](4A0080)Essentia Vitae[#]() harvested by [#](8B0000)+10%%[#](), "
                + "stacking additively. The altar remembers every offering.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "rune_sacrifice")));

    }

    @Override
    protected String entryName() {
        return "Rune of Sacrifice";
    }

    @Override
    protected String entryDescription() {
        return "Amplifies the Essentia Vitae harvested from sacrificed creatures.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_SACRIFICE.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_sacrifice";
    }
}
