package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import net.minecraft.resources.Identifier;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class RitualLedgerEntry extends EntryProvider {

    public RitualLedgerEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Ledger");
        this.pageText("A circle left burning in some forgotten corner still drinks from your [#](4A0080)Anima[#](). The [#](8B0000)Ritual Ledger[#]() keeps account of every ritual you have set in motion.\\\n\\\n"
                + "Use it to list each of your active rituals: its name, the position and dimension of its [#](8B0000)Master Ritual Stone[#](), and the EV it draws each cycle. Click a position to copy it.\\\n\\\n"
                + "[#](2E8B57)Rituals paused by redstone, or sitting in unloaded chunks, are marked as such; neither draws EV while it stays that way.[#]()");

        this.page("crafting", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual_ledger")));
    }

    @Override
    protected String entryName() {
        return "Ritual Ledger";
    }

    @Override
    protected String entryDescription() {
        return "Keep account of every circle you have set burning.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RITUAL_LEDGER.get());
    }

    @Override
    protected String entryId() {
        return "ritual_ledger";
    }
}
