package com.breakinblocks.neovitae.datagen.book.tabula_vitae;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.datagen.book.page.BookTabulaVitaeRecipePageModel;
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
        this.pageText("[#](8B0000)Repairing Salve[#]() mends as it works. Each use of the coated tool restores 1 "
                + "point of durability, allowing favoured instruments to endure far longer than nature intended.\\\n\\\n"
                + "Valid items: anything in [#](8B0000)#neovitae:anointable/weapons[#]().\\\n\\\nApplies: Regular Maintenance I (256 blocks)");

        this.page("recipe1", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/weapon_repair_anointment")
                .withRecipeId2("neovitae:alchemytable/weapon_repair_anointment_l"));
        this.page("recipe2", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/weapon_repair_anointment_2")
                .withRecipeId2("neovitae:alchemytable/weapon_repair_anointment_xl"));
        this.page("recipe3", () -> BookTabulaVitaeRecipePageModel.create()
                .withRecipeId1("neovitae:alchemytable/weapon_repair_anointment_3"));
    }

    @Override
    protected String entryName() {
        return "Repairing Salve";
    }

    @Override
    protected String entryDescription() {
        return "Mends the tool with every stroke.";
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
