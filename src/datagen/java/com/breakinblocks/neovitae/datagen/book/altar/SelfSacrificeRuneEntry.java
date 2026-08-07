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

public class SelfSacrificeRuneEntry extends EntryProvider {

    public SelfSacrificeRuneEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Rune of Self-Sacrifice");
        this.pageText("The [#](8B0000)Rune of Self-Sacrifice[#]() honors the practitioner who spills their own blood "
                + "for the art. Each rune carved into the altar's ring increases the [#](4A0080)Essentia Vitae[#]() "
                + "yielded from your own wounds by [#](8B0000)+10%%[#](), stacking additively. Bleeding into your "
                + "[#](8B0000)Orb of Vitae[#]() and the altar's own draw upon nearby blood mages alike benefit from this rune's blessing.");

        this.page("recipe", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "rune_sacrifice_self")));

    }

    @Override
    protected String entryName() {
        return "Rune of Self-Sacrifice";
    }

    @Override
    protected String entryDescription() {
        return "Rewards your own pain with greater Essentia Vitae from self-inflicted wounds.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.RUNE_SELF_SACRIFICE.asItem());
    }

    @Override
    protected String entryId() {
        return "rune_self_sacrifice";
    }
}
