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
        this.pageText("There are two upgrades currently available for the Routing Network.\\\n\\\n"
                + "Firstly, the **Basic Routing Logic Upgrade** increases the total amount of resources "
                + "transferred per operation. By default, the network transfers 16 items, 1000 mB of fluid, "
                + "and 10,000 FE per cycle. Each upgrade raises all three caps simultaneously.");

        this.page("logic_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Routing Logic Upgrade");
        this.pageText("Craft the Basic Routing Logic Upgrade in the Hellfire Forge.\\\n\\\n"
                + "These upgrades stack to 64, boosting item, fluid, and energy transfer rates. The increased "
                + "throughput is split across all active transfers in the network.");

        this.page("speed_intro", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The second upgrade is the **Basic Routing Speed Upgrade**, which increases the rate at "
                + "which all resources are transferred. The default rate is one operation every 20 ticks (1 "
                + "second), but each speed upgrade will bring this down by 1 tick, to a maximum of every tick "
                + "with 19 upgrades.");

        this.page("speed_recipe", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Basic Routing Speed Upgrade");
        this.pageText("Craft the Basic Routing Speed Upgrade in the Hellfire Forge.\\\n\\\n"
                + "*They stack to 19? That feels... wrong, somehow, but it was this or alter the universe such "
                + "that everything ticks 65 times a second, and that would have made people even more upset.*");
    }

    @Override
    protected String entryName() {
        return "Routing Upgrades";
    }

    @Override
    protected String entryDescription() {
        return "Logic and speed upgrades for routing node networks.";
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
