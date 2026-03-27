package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class WillCatalystsEntry extends EntryProvider {

    public WillCatalystsEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Will Catalysts");
        this.pageText("If [#](8B0000)Spiritus[#]() has one flaw, it is the tedium of collection. Even armed with a "
                + "nearly full [#](8B0000)Spiritus Gem[#](), a [#](8B0000)Sentient Sword[#]() bearing [#](8B0000)Looting III[#]() and "
                + "[#](8B0000)Plunderer's Glint II[#](), the harvest remains a manual labor, and you have far "
                + "grander designs to attend to.");

        this.page("automation", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Fortunately, the entire process can be automated, freeing your hands for worthier "
                + "pursuits.\\\n\\\n"
                + "Begin with four [#](8B0000)Will Crystals[#]() of the same type: Raw, Steadfast, Destructive, "
                + "Vengeful, or Corrosive.");

        this.page("clusters", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystal Clusters");
        this.pageText("Forge a cluster from four matching crystals in the [#](8B0000)Hellfire Forge[#](), then place "
                + "it in any chunk suffused with Will of the same Aspect. In time, new spires crystallize "
                + "and grow, just as they do atop a [#](8B0000)Crystallarium Maleficum[#]().");

        this.page("harvesting", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Crack of the Fractured Crystal[#]() ritual automates the breaking of excess "
                + "spires, and the [#](8B0000)Call of the Zephyr[#]() gathers the dropped crystals.\\\n\\\n"
                + "With a basic [#](8B0000)Routing Node[#]() system, you can even feed surplus crystals back into a "
                + "[#](8B0000)Vas Maleficum[#]() for a self-sustaining loop.\\\n\\\n"
                + "There is, however, a drawback: this process is slow. Each crystal sprouts a new spire once "
                + "every few minutes, consuming nearly as much Will as it yields, roughly [#](B8860B)1 Will per minute "
                + "per spire[#]().");

        this.page("catalyst_recipes", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Will Catalysts");
        this.pageText("Five variants exist, one for each Aspect of Will. Forge them in the "
                + "[#](8B0000)Hellfire Forge[#]().");

        this.page("catalyst_usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("While holding a [#](8B0000)Will Catalyst[#]() of the matching Aspect, right-click a "
                + "[#](8B0000)Crystal Cluster[#]() to supercharge its growth.\\\n\\\n"
                + "Each catalyst reduces the Will cost per spire from 45 to just [#](B8860B)25[#](), and accelerates "
                + "growth tenfold. One dose fuels ten spires of growth, yielding a net bonus of "
                + "[#](B8860B)200 Will per Catalyst[#]().");

        this.page("double_dosing", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A second application extends the effect to 20 growths rather than 10, though it "
                + "provides no other benefit.\\\n\\\n"
                + "The astute blood mage will note this merely trades one manual task, slaying monsters, "
                + "for another, anointing crystals.");

        this.page("full_automation", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Naturally, there is a ritual for that. The [#](8B0000)Gathering of the Forsaken Souls[#]() "
                + "automatically applies catalysts to any clusters within its reach.\\\n\\\n"
                + "With the right farms and a sufficiently ingenious [#](8B0000)Routing Node[#]() configuration, "
                + "the entire apparatus runs itself, top to bottom. The precise arrangement, however, "
                + "is left as an exercise for the ambitious.");
    }

    @Override
    protected String entryName() {
        return "Will Catalysts";
    }

    @Override
    protected String entryDescription() {
        return "Supercharging crystal growth for automated Will production.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RAW_CRYSTAL_CATALYST.get());
    }

    @Override
    protected String entryId() {
        return "will_catalysts";
    }
}
