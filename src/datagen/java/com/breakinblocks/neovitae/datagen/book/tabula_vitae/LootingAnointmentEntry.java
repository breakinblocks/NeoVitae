package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookTabulaVitaeRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class LootingAnointmentEntry extends EntryProvider {

    public LootingAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Plunderer's Glint");
        this.pageText("[#](8B0000)Plunderer's Glint[#]() draws forth spoils from the slain that would otherwise remain "
                + "hidden. The coating stacks with the Looting enchantment.\\\n\\\n"
                + "Valid items: anything in [#](8B0000)#neovitae:anointable/melee[#]().\\\n\\\nApplies: Plundering I (256 hits)");

        this.page("recipe1", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/looting_anointment")
                .withRecipeId2("neovitae:alchemytable/looting_anointment_l"));
        this.page("recipe2", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/looting_anointment_2")
                .withRecipeId2("neovitae:alchemytable/looting_anointment_xl"));
        this.page("recipe3", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/looting_anointment_3"));
    }

    @Override
    protected String entryName() {
        return "Plunderer's Glint";
    }

    @Override
    protected String entryDescription() {
        return "Draws greater spoils from the slain.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.LOOTING_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "looting_anointment";
    }
}
