package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

public class SentientUpgradesEntry extends EntryProvider {

    public SentientUpgradesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sentient Upgrades");
        this.pageText("You have felt it, the armor shifting, adapting, straining to assist you in whatever "
                + "task it observes. It grows in many directions at once, but its capacity is finite. "
                + "Trying to master everything yields mastery of nothing.");

        this.page("specialization", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Consider forging multiple sets, each trained for a specific purpose. A miner's set, "
                + "a warrior's set, an explorer's set; specialization is the path to true power.\\\n\\\n"
                + "You have devised [#](8B0000)Rituals[#]() to assist with focused training, and another "
                + "to imbue your armor with a greater capacity for growth.");

        this.page("training", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("How It Learns");
        this.pageText("The armor banks experience toward a skill each time you perform the deed that "
                + "teaches it, and advances a level once enough has gathered.\\\n\\\n"
                + "[#](B8860B)By magnitude[#](): skills tied to harm, healing, knowledge, or travel learn in "
                + "proportion to the event, the damage taken or dealt, the health mended, the experience "
                + "gathered, the distance crossed. Greater deeds teach it faster.");

        this.page("training2", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](B8860B)By repetition[#](): skills like mining, or enduring fire and venom, learn a "
                + "fixed measure each time, one mark per block broken, or one for every moment wreathed in "
                + "flame or coursing with poison.\\\n\\\n"
                + "Each skill rises at its own thresholds. A [#](8B0000)few[#]() are never taught through "
                + "deeds at all, but inscribed directly from a Tome.");
    }

    @Override
    protected String entryName() {
        return "Sentient Upgrades";
    }

    @Override
    protected String entryDescription() {
        return "The armor learns from your deeds; guide its growth with care.";
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
        return "sentient_upgrades";
    }
}
