package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;

public class UpgradesEntry extends EntryProvider {

    public UpgradesEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Routing Upgrades");
        this.pageText("Two enhancements are currently available for the [#](4A0080)Routing Network[#]().\\\n\\\n"
                + "The [#](8B0000)Basic Routing Logic Upgrade[#]() increases the volume of resources moved per "
                + "operation. By default, the network transfers [#](B8860B)16 items[#](), [#](B8860B)1,000 mB of fluid[#](), "
                + "and [#](B8860B)10,000 FE[#]() per cycle. Each upgrade raises all three caps simultaneously.");

        this.page("logic_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Routing Logic Upgrade");
        this.pageText("Stacks to 64. The increased throughput is distributed across all active transfers "
                + "in the network.");

        this.page("speed_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The second enhancement is the [#](8B0000)Basic Routing Speed Upgrade[#](), which quickens the "
                + "network's pulse. The default rate is one operation every [#](B8860B)20 ticks[#]() (one second). "
                + "Each upgrade reduces this by one tick, to a minimum of every single tick with 19 upgrades.");

        this.page("speed_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Routing Speed Upgrade");
        this.pageText("Stacks to 19 -- one for each tick that can be shaved from the cycle. "
                + "A peculiar limit, perhaps, but the fabric of reality can only be bent so far.");
    }

    @Override
    protected String entryName() {
        return "Routing Upgrades";
    }

    @Override
    protected String entryDescription() {
        return "Enhancing the throughput and tempo of your arcane logistics.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.MASTER_NODE_UPGRADE.get());
    }

    @Override
    protected String entryId() {
        return "upgrades";
    }
}
