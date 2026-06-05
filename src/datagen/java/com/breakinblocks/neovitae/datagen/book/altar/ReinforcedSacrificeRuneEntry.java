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

public class ReinforcedSacrificeRuneEntry extends EntryProvider {

    public ReinforcedSacrificeRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Reinforced Rune of Sacrifice");
        this.pageText("Tempered with [#](8B0000)Netherite Scrap[#]() and [#](8B0000)Intricate Hellforged Parts[#]() from "
                + "the [#](4A0080)Demon Realm[#](), the reinforced rune wrings twice the essence from every "
                + "offering, [#](8B0000)+20%%[#]() per rune, additively. The altar's appetite for sacrifice "
                + "grows ever more insatiable.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "rune_2_sacrifice")));

        this.page("reversion", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Should you have need of the base rune again, the Athanor can strip the "
                + "reinforcement and return it to its original form.[#]()");
    }

    @Override
    protected String entryName() {
        return "Reinforced Rune of Sacrifice";
    }

    @Override
    protected String entryDescription() {
        return "A rune tempered in hellfire, doubling the harvest from slain creatures.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_2_SACRIFICE.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_2_sacrifice";
    }
}
