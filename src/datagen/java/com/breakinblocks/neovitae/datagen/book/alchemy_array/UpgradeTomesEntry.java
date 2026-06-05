package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class UpgradeTomesEntry extends EntryProvider {

    public UpgradeTomesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Upgrade Tomes");
        this.pageText("[#](4A0080)Upgrade Tomes[#]() are crystallized records of a particular skill, each carrying "
                + "the experience needed to inscribe one or more levels of its upgrade onto a chestplate. They "
                + "come from three places:\\\n\\\n"
                + "- The loot tables of [#](8B0000)The Mines[#]() (ore chambers, the Smithy, Foreman's hoard).\n\n"
                + "- The [#](8B0000)Sentient Extraction[#]() ritual, which extracts the upgrades from a "
                + "thrown sentient armor piece as tomes.\n\n"
                + "- Combining two tomes of the same upgrade at a vanilla crafting table.\\\n\\\n"
                + "Press [Use] while holding a tome to inscribe one level of that skill onto your worn chestplate, "
                + "assuming sufficient [#](B8860B)Upgrade Points[#]() remain. Hold sneak and press [Use] to "
                + "inscribe as much as possible at once.");

        this.page("consumption", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](4A0080)Tome[#]() is consumed in the process, transferring all stored experience "
                + "to the chestplate. If insufficient [#](B8860B)Upgrade Points[#]() remain, the tome applies "
                + "what it can and retains the rest, unless less than one level's worth remains, in which "
                + "case the tome crumbles to nothing.");

        this.page("strategy", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Tomes are the key to deliberate specialization. Focus your training on the abilities "
                + "that serve you best, and discard the rest.\\\n\\\n"
                + "[#](2E8B57)Consider maintaining separate chestplates for different purposes, one for "
                + "mining, another for combat, a third for exploration. Each can be trained with its own "
                + "set of tomes.[#]()");
    }

    @Override
    protected String entryName() {
        return "Upgrade Tomes";
    }

    @Override
    protected String entryDescription() {
        return "Crystallized knowledge, inscribe specific skills onto your living armour.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.UPGRADE_TOME.get());
    }

    @Override
    protected String entryId() {
        return "upgrade_tomes";
    }
}
