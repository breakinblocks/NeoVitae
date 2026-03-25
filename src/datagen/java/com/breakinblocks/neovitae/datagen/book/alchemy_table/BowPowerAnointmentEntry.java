package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class BowPowerAnointmentEntry extends EntryProvider {

    public BowPowerAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron Tip");
        this.pageText("Craft **Iron Tip** in the Alchemy Table (recipe: neovitae:alchemytable/bow_power_anointment). "
                + "Increases the damage of fired arrows by 25%. Also stacks with Vanilla enchantments.\\\n\\\n"
                + "Valid items: Bows, Crossbows.\\\n\\\nApplies: Heavy Shot I (256 shots)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron Tip L");
        this.pageText("Craft **Iron Tip L** in the Alchemy Table (recipe: neovitae:alchemytable/bow_power_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\\\n\\\nApplies: Heavy Shot I (1024 shots)");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron Tip II");
        this.pageText("Craft **Iron Tip II** in the Alchemy Table (recipe: neovitae:alchemytable/bow_power_anointment_2). "
                + "This upgraded version of the anointment increases the damage by 50% instead.\\\n\\\n"
                + "Applies: Heavy Shot II (256 shots)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron Tip XL");
        this.pageText("Craft **Iron Tip XL** in the Alchemy Table (recipe: neovitae:alchemytable/bow_power_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\\\n\\\nApplies: Heavy Shot I (4096 shots)");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Iron Tip III");
        this.pageText("Craft **Iron Tip III** in the Alchemy Table (recipe: neovitae:alchemytable/bow_power_anointment_3). "
                + "This upgraded version of the anointment increases the damage by 75% instead.\\\n\\\n"
                + "Applies: Heavy Shot III (256 shots)");
    }

    @Override
    protected String entryName() {
        return "Iron Tip";
    }

    @Override
    protected String entryDescription() {
        return "Increases arrow damage.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.BOW_POWER_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "bow_power_anointment";
    }
}
