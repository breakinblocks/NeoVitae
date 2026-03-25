package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class BowVelocityAnointmentEntry extends EntryProvider {

    public BowVelocityAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Archer's Polish");
        this.pageText("Craft **Archer's Polish** in the Alchemy Table (recipe: neovitae:alchemytable/bow_velocity_anointment). "
                + "Increases the velocity of fired arrows by 50%. This also increases the damage dealt by your "
                + "arrows proportionally. Also stacks with Vanilla enchantments.\\\n\\\n"
                + "Valid items: Bows, Crossbows.\\\n\\\nApplies: Sniping I (256 shots)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Archer's Polish L");
        this.pageText("Craft **Archer's Polish L** in the Alchemy Table (recipe: neovitae:alchemytable/bow_velocity_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\\\n\\\nApplies: Sniping I (1024 shots)");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Archer's Polish II");
        this.pageText("Craft **Archer's Polish II** in the Alchemy Table (recipe: neovitae:alchemytable/bow_velocity_anointment_2). "
                + "This upgraded version of the anointment increases the velocity by 100% instead.\\\n\\\n"
                + "Applies: Sniping II (256 shots)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Archer's Polish XL");
        this.pageText("Craft **Archer's Polish XL** in the Alchemy Table (recipe: neovitae:alchemytable/bow_velocity_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\\\n\\\nApplies: Sniping I (4096 shots)");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Archer's Polish III");
        this.pageText("Craft **Archer's Polish III** in the Alchemy Table (recipe: neovitae:alchemytable/bow_velocity_anointment_3). "
                + "This upgraded version of the anointment increases the velocity by 150% instead.\\\n\\\n"
                + "Applies: Sniping III (256 shots)");
    }

    @Override
    protected String entryName() {
        return "Archer's Polish";
    }

    @Override
    protected String entryDescription() {
        return "Increases arrow velocity.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.BOW_VELOCITY_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "bow_velocity_anointment";
    }
}
