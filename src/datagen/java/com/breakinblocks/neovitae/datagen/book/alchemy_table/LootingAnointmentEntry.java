package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookAlchemyTableRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

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
        this.pageText("Craft [#](8B0000)Plunderer's Glint[#]() in the Alchemy Table. "
                + "Increases the drops from killed mobs. Also stacks with the Looting enchantment.\\\n\\\n"
                + "Valid items: Tools, Swords.\\\n\\\nApplies: Plundering I (256 hits)");

        this.page("recipe1", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/looting_anointment")
                .withRecipeId2("neovitae:alchemytable/looting_anointment_l"));
        this.page("recipe2", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/looting_anointment_2")
                .withRecipeId2("neovitae:alchemytable/looting_anointment_xl"));
        this.page("recipe3", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/looting_anointment_3"));
    }

    @Override
    protected String entryName() {
        return "Plunderer's Glint";
    }

    @Override
    protected String entryDescription() {
        return "Increases drops from killed mobs.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
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
