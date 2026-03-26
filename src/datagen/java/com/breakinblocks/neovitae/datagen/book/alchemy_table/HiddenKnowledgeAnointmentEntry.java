package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookAlchemyTableRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class HiddenKnowledgeAnointmentEntry extends EntryProvider {

    public HiddenKnowledgeAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Miner's Secrets");
        this.pageText("[#](8B0000)Miner's Secrets[#]() causes exp-dropping blocks to drop extra exp on successful harvest. Drops +2exp per block. "
                + "Consumed when extra exp dropped."
                + "\\\n\\\nValid items: Tools, Swords.\\\n\\\nApplies: Miner's Secrets (256 exp-dropping blocks)");

        this.page("recipe1", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/hidden_knowledge_anointment")
                .withRecipeId2("neovitae:alchemytable/hidden_knowledge_anointment_l"));
        this.page("recipe2", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/hidden_knowledge_anointment_2")
                .withRecipeId2("neovitae:alchemytable/hidden_knowledge_anointment_xl"));
        this.page("recipe3", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/hidden_knowledge_anointment_3"));
    }

    @Override
    protected String entryName() {
        return "Miner's Secrets";
    }

    @Override
    protected String entryDescription() {
        return "Extra experience from mining blocks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.HIDDEN_KNOWLEDGE_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "hidden_knowledge_anointment";
    }
}
