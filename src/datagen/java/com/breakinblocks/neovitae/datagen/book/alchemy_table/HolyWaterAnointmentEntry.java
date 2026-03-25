package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class HolyWaterAnointmentEntry extends EntryProvider {

    public HolyWaterAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Holy Water");
        this.pageText("Craft **Holy Water** in the Alchemy Table (recipe: neovitae:alchemytable/holy_water_anointment). "
                + "Temporarily increases the melee damage dealt to undead mobs by +5.\n\n"
                + "Valid items: Tools, Swords.\n\nApplies: Holy Light I (256 hits)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Holy Water L");
        this.pageText("Craft **Holy Water L** in the Alchemy Table (recipe: neovitae:alchemytable/holy_water_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\n\nApplies: Holy Light I (1024 hits)");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Holy Water II");
        this.pageText("Craft **Holy Water II** in the Alchemy Table (recipe: neovitae:alchemytable/holy_water_anointment_2). "
                + "This upgraded version of the anointment increases melee damage dealt to undead mobs by +10.\n\n"
                + "Applies: Holy Light II (256 hits)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Holy Water XL");
        this.pageText("Craft **Holy Water XL** in the Alchemy Table (recipe: neovitae:alchemytable/holy_water_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\n\nApplies: Holy Light I (4096 hits)");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Holy Water III");
        this.pageText("Craft **Holy Water III** in the Alchemy Table (recipe: neovitae:alchemytable/holy_water_anointment_3). "
                + "This upgraded version of the anointment increases melee damage dealt to undead mobs by +15.\n\n"
                + "Applies: Holy Light III (256 hits)");
    }

    @Override
    protected String entryName() {
        return "Holy Water";
    }

    @Override
    protected String entryDescription() {
        return "Increases damage dealt to undead mobs.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.HOLY_WATER_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "holy_water_anointment";
    }
}
