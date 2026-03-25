package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SilkTouchAnointmentEntry extends EntryProvider {

    public SilkTouchAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Soft Coating");
        this.pageText("Craft **Soft Coating** in the Alchemy Table (recipe: neovitae:alchemytable/silk_touch_anointment). "
                + "Applies Silk Touch to blocks harvested. Does not stack with the vanilla enchantment.\n\n"
                + "Valid items: Tools, Swords, Charges.\n\nApplies: Soft Touch I (256 blocks)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Soft Coating L");
        this.pageText("Craft **Soft Coating L** in the Alchemy Table (recipe: neovitae:alchemytable/silk_touch_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\n\nApplies: Soft Touch I (1024 blocks)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Soft Coating XL");
        this.pageText("Craft **Soft Coating XL** in the Alchemy Table (recipe: neovitae:alchemytable/silk_touch_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\n\nApplies: Soft Touch I (4096 blocks)");
    }

    @Override
    protected String entryName() {
        return "Soft Coating";
    }

    @Override
    protected String entryDescription() {
        return "Applies Silk Touch to harvested blocks.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SILK_TOUCH_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "silk_touch_anointment";
    }
}
