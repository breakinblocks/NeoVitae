package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class AnointmentsEntry extends EntryProvider {

    public AnointmentsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Anointments");
        this.pageText("Where flasks empower the body, [#](8B0000)Anointments[#]() empower the hand. These are "
                + "[#](4A0080)alchemical coatings[#]() for your tools, weapons, and [#](8B0000)Charges[#](). Hold the anointment "
                + "in one hand and press Use to apply it to the item in your other hand. Not every coating "
                + "suits every instrument -- choose wisely."
                + "\\\n\\\nUnlike elixirs, anointments are not governed by time. They diminish with each use of "
                + "the coated tool, fading stroke by stroke until spent.");

        this.page("slate_vial", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Slate-infused Vial");
        this.pageText("The [#](8B0000)Slate-infused Vial[#]() is prepared at the Tabula Vitae (recipe: neovitae:alchemytable/slate_vial). "
                + "Ordinary glass cannot withstand the reactive compounds within an anointment. This vial, "
                + "reinforced with powdered slate, contains what lesser vessels cannot.");

        this.page("smithing", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("An [#](8B0000)anointment[#]() may also be applied via a smithing table, where it takes the place "
                + "of a smithing template. This proves especially useful for two-handed weapons and other "
                + "situations where one's hands are otherwise occupied.");
    }

    @Override
    protected String entryName() {
        return "Anointments";
    }

    @Override
    protected String entryDescription() {
        return "Alchemical coatings for tools and weapons.";
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
        return "anointments";
    }
}
