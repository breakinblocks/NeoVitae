package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class FortuneAnointmentEntry extends EntryProvider {

    public FortuneAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fortuna Extract");
        this.pageText("Craft **Fortuna Extract** in the Alchemy Table (recipe: neovitae:alchemytable/fortune_anointment). "
                + "Increases the yield of some harvested blocks. Also stacks with the Fortune enchantment.\\\n\\\n"
                + "Valid items: Tools, Swords, Charges.\\\n\\\nApplies: Fortunate I (256 blocks)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fortuna Extract L");
        this.pageText("Craft **Fortuna Extract L** in the Alchemy Table (recipe: neovitae:alchemytable/fortune_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\\\n\\\nApplies: Fortunate I (1024 blocks)");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fortuna Extract II");
        this.pageText("Craft **Fortuna Extract II** in the Alchemy Table (recipe: neovitae:alchemytable/fortune_anointment_2). "
                + "This upgraded version of the anointment increases the yield.\\\n\\\nApplies: Fortunate II (256 blocks)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fortuna Extract XL");
        this.pageText("Craft **Fortuna Extract XL** in the Alchemy Table (recipe: neovitae:alchemytable/fortune_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\\\n\\\nApplies: Fortunate I (4096 blocks)");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Fortuna Extract III");
        this.pageText("Craft **Fortuna Extract III** in the Alchemy Table (recipe: neovitae:alchemytable/fortune_anointment_3). "
                + "This upgraded version of the anointment increases the yield.\\\n\\\nApplies: Fortunate III (256 blocks)");
    }

    @Override
    protected String entryName() {
        return "Fortuna Extract";
    }

    @Override
    protected String entryDescription() {
        return "Increases yield from harvested blocks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.FORTUNE_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "fortune_anointment";
    }
}
