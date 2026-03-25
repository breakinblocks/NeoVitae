package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class HiddenKnowledgeAnointmentEntry extends EntryProvider {

    public HiddenKnowledgeAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Miner's Secrets");
        this.pageText("Craft **Miner's Secrets** in the Alchemy Table (recipe: neovitae:alchemytable/hidden_knowledge_anointment). "
                + "Exp-dropping blocks drop extra exp on successful harvest. Drops +2exp per block. "
                + "Consumed when extra exp dropped.\\\n\\\n"
                + "Valid items: Tools, Swords.\\\n\\\nApplies: Miner's Secrets I (256 exp-dropping blocks)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Miner's Secrets L");
        this.pageText("Craft **Miner's Secrets L** in the Alchemy Table (recipe: neovitae:alchemytable/hidden_knowledge_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\\\n\\\n"
                + "Applies: Miner's Secrets I (1024 exp-dropping blocks)");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Miner's Secrets II");
        this.pageText("Craft **Miner's Secrets II** in the Alchemy Table (recipe: neovitae:alchemytable/hidden_knowledge_anointment_2). "
                + "This upgraded version of the anointment increases the yield to +4exp per block.\\\n\\\n"
                + "Applies: Miner's Secrets II (256 exp-dropping blocks)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Miner's Secrets XL");
        this.pageText("Craft **Miner's Secrets XL** in the Alchemy Table (recipe: neovitae:alchemytable/hidden_knowledge_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\\\n\\\n"
                + "Applies: Miner's Secrets I (4096 exp-dropping blocks)");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Miner's Secrets III");
        this.pageText("Craft **Miner's Secrets III** in the Alchemy Table (recipe: neovitae:alchemytable/hidden_knowledge_anointment_3). "
                + "This upgraded version of the anointment increases the yield to +6exp per block.\\\n\\\n"
                + "Applies: Miner's Secrets III (256 exp-dropping blocks)");
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
