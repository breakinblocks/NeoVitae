package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class WeaponRepairAnointmentEntry extends EntryProvider {

    public WeaponRepairAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("base", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Repairing Salve");
        this.pageText("Craft **Repairing Salve** in the Alchemy Table (recipe: neovitae:alchemytable/weapon_repair_anointment). "
                + "Repairs damaged tools by 1 point when tool is used.\n\n"
                + "Valid items: Tools, Swords.\n\nApplies: Regular Maintenance I (256 blocks)");

        this.page("long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Repairing Salve L");
        this.pageText("Craft **Repairing Salve L** in the Alchemy Table (recipe: neovitae:alchemytable/weapon_repair_anointment_l). "
                + "This upgraded version of the anointment lasts four times longer.\n\n"
                + "Applies: Regular Maintenance I (1024 blocks)");

        this.page("tier2", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Repairing Salve II");
        this.pageText("Craft **Repairing Salve II** in the Alchemy Table (recipe: neovitae:alchemytable/weapon_repair_anointment_2). "
                + "This upgraded version of the anointment repairs the tool by 2 points for every point of damage "
                + "taken. Does not activate if doing so would waste some of the repair bonus.\n\n"
                + "Applies: Regular Maintenance II (256 blocks)");

        this.page("extra_long", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Repairing Salve XL");
        this.pageText("Craft **Repairing Salve XL** in the Alchemy Table (recipe: neovitae:alchemytable/weapon_repair_anointment_xl). "
                + "This upgraded version of the anointment lasts sixteen times longer.\n\n"
                + "Applies: Regular Maintenance I (4096 blocks)");

        this.page("tier3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Repairing Salve III");
        this.pageText("Craft **Repairing Salve III** in the Alchemy Table (recipe: neovitae:alchemytable/weapon_repair_anointment_3). "
                + "This upgraded version of the anointment repairs the tool by 3 points for every point of damage "
                + "taken. Does not activate if doing so would waste some of the repair bonus.\n\n"
                + "Applies: Regular Maintenance III (256 blocks)");
    }

    @Override
    protected String entryName() {
        return "Repairing Salve";
    }

    @Override
    protected String entryDescription() {
        return "Repairs damaged tools during use.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.WEAPON_REPAIR_ANOINTMENT.get());
    }

    @Override
    protected String entryId() {
        return "weapon_repair_anointment";
    }
}
