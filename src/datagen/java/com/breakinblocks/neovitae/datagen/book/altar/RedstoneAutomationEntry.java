package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RedstoneAutomationEntry extends EntryProvider {

    public RedstoneAutomationEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Redstone and Automation");
        this.pageText("The **Blood Altar** is a fantastic tool, but standing around and waiting for slates to "
                + "craft is not your idea of a good time. Luckily, items and **LP** can be automatically piped "
                + "in and out of the altar, albeit with a few caveats.\n\nWhile a simple **Hopper** lets you "
                + "pipe items in, the Altar won't stop it from inputting more than 1 at a time. It will happily "
                + "craft 64 slates in one");

        this.page("stacking", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("go, consuming 64 times as much **LP** as usual to do so - but if you can't supply said "
                + "**LP** fast enough, you're going to run into trouble.\n\nAdditionally, the altar makes no "
                + "distinction between input and output, so without some sort of filter, items will be pulled in "
                + "and out as fast as your item transfer system can handle. Perhaps a look at the **Routing Nodes** "
                + "will be helpful...");

        this.page("fluid_transfer", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The altar also supports the transfer of **Life Essence**, both to and from an external "
                + "tank. Simply hook up your fluid pipe of choice and you can store excess **Life Essence** for "
                + "later crafts.\n\nNote that the transfer speed is very slow by default. If you want to speed it "
                + "up, you'd best look into **Acceleration Runes** and **Displacement Runes**.");

        this.page("internal_tank", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Don't forget that this **Life Essence** isn't taken directly from the altar itself, but "
                + "rather from a second, secret internal tank. This tank can hold **up to 10%** of the **Life "
                + "Essence** that the altar itself can, so if the numbers don't appear to be adding up exactly "
                + "right, or if **Life Essence** appears to be vanishing from your altar, this is probably where "
                + "it's going. The same limitations apply to **Life Essence** being piped in.");

        this.page("comparator", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The amount of **Life Essence** in the **Blood Altar** can be read via a comparator on "
                + "the side, similarly to a vanilla chest.\n\nIf you place a **Bloodstone Brick** underneath the "
                + "altar, the comparator will instead read the value of the **Soul Network** of the owner of any "
                + "orb that is placed into the Altar.\n\nThe signal strength depends on the size of the orb in "
                + "the altar, not the maximum **LP** of the network.");

        this.page("comparator_examples", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("For example, if you have **500,000 LP**, a Weak Blood Orb would show as completely full, "
                + "but a Master Blood Orb would show as only half full. This can be used to, for example, "
                + "deactivate certain rituals when you are running low on **LP**, to ensure you don't run out."
                + "\n\nLastly, placing a **Redstone Lamp** underneath the altar will make it output a redstone "
                + "signal upon finishing a crafting operation.");
    }

    @Override
    protected String entryName() {
        return "Redstone and Automation";
    }

    @Override
    protected String entryDescription() {
        return "Automating the Blood Altar with hoppers, pipes, and comparators.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.REDSTONE);
    }

    @Override
    protected String entryId() {
        return "redstone_automation";
    }
}
