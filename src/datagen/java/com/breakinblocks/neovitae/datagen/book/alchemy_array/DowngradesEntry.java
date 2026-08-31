package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class DowngradesEntry extends EntryProvider {

    public DowngradesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Downgrades");
        this.pageText("The armor can be taught weakness as deliberately as strength. A "
                + "[#](8B0000)Downgrade[#]() is a curse inscribed onto your Sentient chestplate: it "
                + "hobbles you in one respect, and in exchange carries a [#](4A0080)negative point "
                + "cost[#](), freeing Upgrade Points to spend on the abilities you actually want.\\\n\\\n"
                + "A chestplate burdened with the right curses can hold upgrades well beyond its "
                + "normal capacity.");

        this.page("applying", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Taking On a Curse");
        this.pageText("Each downgrade is invoked through a mundane [#](8B0000)catalyst[#]() item, "
                + "listed on the pages that follow. Build the [#](8B0000)Ritual of Sentient "
                + "Penance[#]() (see the Rituals chapter), stand on the Master Ritual Stone wearing "
                + "your Sentient set, and throw the catalyst onto the stone. Each catalyst consumed "
                + "inscribes one level of its curse.\\\n\\\n"
                + "To lift a curse, run the chestplate through the [#](8B0000)Sentient "
                + "Extraction[#]() ritual; the downgrade returns as a tome alongside your upgrades.");
    }

    @Override
    protected String entryName() {
        return "Downgrades";
    }

    @Override
    protected String entryDescription() {
        return "Trade deliberate weakness for room to grow.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WITHER_ROSE);
    }

    @Override
    protected String entryId() {
        return "downgrades";
    }
}
