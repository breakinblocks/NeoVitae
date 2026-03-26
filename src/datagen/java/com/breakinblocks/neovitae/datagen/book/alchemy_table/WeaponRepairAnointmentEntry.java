package com.breakinblocks.neovitae.datagen.book.alchemy_table;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookAlchemyTableRecipePageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class WeaponRepairAnointmentEntry extends EntryProvider {

    public WeaponRepairAnointmentEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Repairing Salve");
        this.pageText("Craft [#](8B0000)Repairing Salve[#]() in the Alchemy Table. "
                + "Repairs damaged tools by 1 point when tool is used.\\\n\\\n"
                + "Valid items: Tools, Swords.\\\n\\\nApplies: Regular Maintenance I (256 blocks)");

        this.page("recipe1", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/weapon_repair_anointment")
                .withRecipeId2("neovitae:alchemytable/weapon_repair_anointment_l"));
        this.page("recipe2", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/weapon_repair_anointment_2")
                .withRecipeId2("neovitae:alchemytable/weapon_repair_anointment_xl"));
        this.page("recipe3", () -> BookAlchemyTableRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/weapon_repair_anointment_3"));
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
