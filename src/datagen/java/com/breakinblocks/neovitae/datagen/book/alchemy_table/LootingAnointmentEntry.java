package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class LootingAnointmentEntry extends EntryProvider {

    public LootingAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Plunderer's Glint");
        this.pageText("Craft **Plunderer's Glint** in the Alchemy Table (recipe: neovitae:alchemytable/looting_anointment). "
                + "Increases the drops from killed mobs. Also stacks with the Looting enchantment.\\\n\\\n"
                + "Valid items: Tools, Swords.\\\n\\\nApplies: Plundering I (256 hits)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Plunderer's Glint L");
        this.pageText("Craft **Plunderer's Glint L** in the Alchemy Table (recipe: neovitae:alchemytable/looting_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\\\n\\\nApplies: Plundering I (1024 hits)");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Plunderer's Glint II");
        this.pageText("Craft **Plunderer's Glint II** in the Alchemy Table (recipe: neovitae:alchemytable/looting_anointment_2). "
                + "This upgraded version of the anointment further increases the drops from killed mobs.\\\n\\\n"
                + "Applies: Plundering II (256 hits)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Plunderer's Glint XL");
        this.pageText("Craft **Plunderer's Glint XL** in the Alchemy Table (recipe: neovitae:alchemytable/looting_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\\\n\\\nApplies: Plundering I (4096 hits)");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Plunderer's Glint III");
        this.pageText("Craft **Plunderer's Glint III** in the Alchemy Table (recipe: neovitae:alchemytable/looting_anointment_3). "
                + "This upgraded version of the anointment further increases the drops from killed mobs.\\\n\\\n"
                + "Applies: Plundering III (256 hits)");
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
