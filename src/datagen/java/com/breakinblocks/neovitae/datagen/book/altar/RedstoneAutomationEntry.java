package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import net.minecraft.world.item.Items;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;

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
        this.pageText("The [#](8B0000)Ara Vitae[#]() is a marvel, but even a devoted Vitaemancer tires of standing "
                + "vigil over every slate. Fortunately, the altar accepts mechanical servants; items and "
                + "[#](4A0080)Essentia Vitae[#]() can be piped in and out.\\\n\\\nThe basin holds a [#](8B0000)single "
                + "item[#]() at a time. A simple [#](8B0000)Hopper[#]() feeds them in one by one, and the altar");

        this.page("stacking", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("transmutes each in turn, drawing [#](4A0080)Essentia Vitae[#]() as it works. Keep the basin "
                + "supplied with essence and it labors without pause.\\\n\\\nThe altar makes no distinction between input "
                + "and output. Without a filter, items will cycle in and out as fast as your transfer system allows. "
                + "The [#](8B0000)Routing Nodes[#]() may serve you well here.");

        this.page("fluid_transfer", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The altar also permits the transfer of [#](4A0080)Essentia Vitae[#]() to and from external tanks. "
                + "Connect your fluid conduit of choice and you can stockpile excess essence for later workings."
                + "\\\n\\\n[#](2E8B57)The flow is sluggish by default. Acceleration Runes quicken the altar's pulse, "
                + "while Displacement Runes widen the channel through which essence flows.[#]()");

        this.page("internal_tank", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Remember: the [#](4A0080)Essentia Vitae[#]() piped in or out does not come directly from the "
                + "altar's main basin. A hidden secondary reservoir, holding [#](8B0000)up to 10%%[#]() of the altar's "
                + "total capacity, acts as the intermediary. If essence seems to vanish from the basin without "
                + "explanation, or the numbers never quite add up, this phantom vessel is the culprit.");

        this.page("comparator", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A comparator placed beside the [#](8B0000)Ara Vitae[#]() reads the level of "
                + "[#](4A0080)Essentia Vitae[#]() within, much like a chest.\\\n\\\nPlace a [#](8B0000)Blood Stained Glass[#]() "
                + "block directly beneath the altar, and the comparator instead reads the [#](4A0080)Anima[#]() of "
                + "whoever owns the orb resting in the altar's basin. The signal strength scales to the orb's "
                + "tier, not the maximum capacity of the network.");

        this.page("comparator_examples", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("For example, [#](8B0000)500,000 EV[#]() would register as overflowing through a Weak "
                + "Novicius Orb, but only half-strength through a Magus Orb. Clever use of this lets you "
                + "shut down costly rituals before your [#](4A0080)Anima[#]() runs dry.\\\n\\\n"
                + "[#](2E8B57)When the orb is bound to a team via NeoVitae Teams, the comparator reads the "
                + "team's pooled Anima automatically; no special setup is needed.[#]()\\\n\\\n"
                + "[#](2E8B57)Place a Redstone Lamp beneath the altar and it will emit a redstone pulse the instant "
                + "a transmutation completes, useful for chained automation.[#]()");
    }

    @Override
    protected String entryName() {
        return "Redstone and Automation";
    }

    @Override
    protected String entryDescription() {
        return "Mechanical servants for the Ara Vitae: hoppers, pipes, and comparators.";
    }

    @Override
    protected GuiSprite entryBackground() {
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
