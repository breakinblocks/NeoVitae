package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookAlchemyTableRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

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
        this.pageText("Craft [#](8B0000)Honing Oil[#]() in the Alchemy Table. "
                + "Temporarily increases the melee damage dealt by +3.\\\n\\\n"
                + "Valid items: Tools, Swords.\\\n\\\nApplies: Whetstone I (256 hits)");

        this.page("recipe1", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/melee_damage_anointment")
                .withRecipeId2("neovitae:alchemytable/melee_damage_anointment_l"));
        this.page("recipe2", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/melee_damage_anointment_2")
                .withRecipeId2("neovitae:alchemytable/melee_damage_anointment_xl"));
        this.page("recipe3", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/melee_damage_anointment_3"));
    }

    @Override
    protected String entryName() {
        return "Honing Oil";
    }

    @Override
    protected String entryDescription() {
        return "Increases melee damage dealt.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
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
