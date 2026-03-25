package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class SlatesEntry extends EntryProvider {

    public SlatesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tiers of Slates");
        this.pageText("The **Blood Altar**'s main use is the production of **Slates**. Each tier of slate "
                + "requires the previous tier and a more powerful altar than the last.");

        this.page("blank_reinforced", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Craft the **Blank Slate** in the Blood Altar (Tier 1, cost: 1,000 LP). "
                + "Uses a Smooth Stone as the input.\\\n\\\n"
                + "Craft the **Reinforced Slate** in the Blood Altar (Tier 2, cost: 2,000 LP). "
                + "Uses a Blank Slate as the input.");

        this.page("imbued_demonic", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Craft the **Imbued Slate** in the Blood Altar (Tier 3, cost: 5,000 LP). "
                + "Uses a Reinforced Slate as the input.\\\n\\\n"
                + "Craft the **Demonic Slate** in the Blood Altar (Tier 4, cost: 15,000 LP). "
                + "Uses an Imbued Slate as the input.");

        this.page("ethereal", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Craft the **Ethereal Slate** in the Blood Altar (Tier 5, cost: 30,000 LP). "
                + "Uses a Demonic Slate as the input.");
    }

    @Override
    protected String entryName() {
        return "Tiers of Slates";
    }

    @Override
    protected String entryDescription() {
        return "Crafting components produced in the Blood Altar at increasing tiers.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SLATE_BLANK.get());
    }

    @Override
    protected String entryId() {
        return "slates";
    }
}
