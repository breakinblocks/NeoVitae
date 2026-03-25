package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class MeleeAnointmentEntry extends EntryProvider {

    public MeleeAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Honing Oil");
        this.pageText("Craft **Honing Oil** in the Alchemy Table (recipe: neovitae:alchemytable/melee_damage_anointment). "
                + "Temporarily increases the melee damage dealt by +3.\n\n"
                + "Valid items: Tools, Swords.\n\nApplies: Whetstone I (256 hits)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Honing Oil L");
        this.pageText("Craft **Honing Oil L** in the Alchemy Table (recipe: neovitae:alchemytable/melee_damage_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\n\nApplies: Whetstone I (1024 hits)");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Honing Oil II");
        this.pageText("Craft **Honing Oil II** in the Alchemy Table (recipe: neovitae:alchemytable/melee_damage_anointment_2). "
                + "This upgraded version of the anointment increases melee damage dealt by +6.\n\nApplies: Whetstone II (256 hits)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Honing Oil XL");
        this.pageText("Craft **Honing Oil XL** in the Alchemy Table (recipe: neovitae:alchemytable/melee_damage_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\n\nApplies: Whetstone I (4096 hits)");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Honing Oil III");
        this.pageText("Craft **Honing Oil III** in the Alchemy Table (recipe: neovitae:alchemytable/melee_damage_anointment_3). "
                + "This upgraded version of the anointment increases melee damage dealt by +9.\n\nApplies: Whetstone III (256 hits)");
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
