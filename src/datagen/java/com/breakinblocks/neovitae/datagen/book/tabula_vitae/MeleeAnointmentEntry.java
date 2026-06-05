package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookTabulaVitaeRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class MeleeAnointmentEntry extends EntryProvider {

    public MeleeAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Honing Oil");
        this.pageText("[#](8B0000)Honing Oil[#]() sharpens the edge of any blade it touches to a lethal keenness, "
                + "adding 3 points of melee damage per strike.\\\n\\\n"
                + "Valid items: anything in [#](8B0000)#neovitae:anointable/melee[#]().\\\n\\\nApplies: Whetstone I (256 hits)");

        this.page("recipe1", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/melee_damage_anointment")
                .withRecipeId2("neovitae:alchemytable/melee_damage_anointment_l"));
        this.page("recipe2", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/melee_damage_anointment_2")
                .withRecipeId2("neovitae:alchemytable/melee_damage_anointment_xl"));
        this.page("recipe3", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/melee_damage_anointment_3"));
    }

    @Override
    protected String entryName() {
        return "Honing Oil";
    }

    @Override
    protected String entryDescription() {
        return "Hones the blade to a lethal keenness.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.MELEE_DAMAGE_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "melee_anointment";
    }
}
