package com.breakinblocks.neovitae.datagen.book.alchemy_table;

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
        this.pageText("**Anointments** are essentially **potions** for your tools, weapons, and even your "
                + "**Charges**. By pressing the Use key with the **anointment** in one hand, it will be applied "
                + "to the item in your other hand, if possible. Not all anointments work on all equipment!"
                + "\\\n\\\nUnlike **potions**, **anointments** aren't timed; rather, they wear off a bit at a time "
                + "as your tool or weapon is used.");

        this.page("slate_vial", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Slate-infused Vial");
        this.pageText("Craft the **Slate-infused Vial** in the Alchemy Table (recipe: neovitae:alchemytable/slate_vial). "
                + "A sturdy Vial that can contain otherwise uncontainable mixtures.");

        this.page("smithing", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("It is also possible to apply an **anointment** to an applicable item using a smithing table. "
                + "This is particularly useful for two-handed weapons or other situations where you might find "
                + "yourself not having enough hands. The **anointment** takes the role of a smithing template "
                + "in this process.");
    }

    @Override
    protected String entryName() {
        return "Anointments";
    }

    @Override
    protected String entryDescription() {
        return "Potions for your tools and weapons.";
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
