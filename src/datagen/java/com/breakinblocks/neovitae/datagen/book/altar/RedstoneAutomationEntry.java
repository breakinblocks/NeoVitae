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
        this.pageText("The [#](8B0000)Ara Vitae[#]() is a marvel, but even a devoted Vitaemancer tires of standing "
                + "vigil over every slate. Fortunately, the altar accepts mechanical servants -- items and "
                + "[#](4A0080)Essentia Vitae[#]() can be piped in and out, though the altar's hunger has its own "
                + "peculiarities.\\\n\\\nA simple [#](8B0000)Hopper[#]() feeds items into the basin, but it knows no restraint. "
                + "It will happily deposit an entire stack, and the altar will");

        this.page("stacking", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("attempt to transmute all of them at once -- consuming [#](8B0000)Essentia Vitae[#]() for every item in "
                + "the stack. If you cannot supply that much [#](4A0080)Essentia Vitae[#]() quickly enough, the working "
                + "will stall and progress will bleed away.\\\n\\\nThe altar also makes no distinction between input "
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
                + "altar's main basin. A hidden secondary reservoir -- holding [#](8B0000)up to 10%%[#]() of the altar's "
                + "total capacity -- acts as the intermediary. If essence seems to vanish from the basin without "
                + "explanation, or the numbers never quite add up, this phantom vessel is the culprit.");

        this.page("comparator", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("A comparator placed beside the [#](8B0000)Ara Vitae[#]() reads the level of "
                + "[#](4A0080)Essentia Vitae[#]() within, much like a chest.\\\n\\\nPlace a [#](8B0000)Bloodstone Brick[#]() "
                + "beneath the altar, and the comparator instead reads the [#](4A0080)Anima[#]() of whoever owns the "
                + "orb resting inside. The signal strength scales to the orb's tier, not the maximum capacity "
                + "of the network.");

        this.page("comparator_examples", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("For example, [#](8B0000)500,000 EV[#]() would register as overflowing through a Weak "
                + "Blood Orb, but only half-strength through a Master Blood Orb. Clever use of this lets you "
                + "shut down costly rituals before your [#](4A0080)Anima[#]() runs dry.\\\n\\\n"
                + "[#](2E8B57)Place a Redstone Lamp beneath the altar and it will emit a redstone pulse the instant "
                + "a transmutation completes -- useful for chained automation.[#]()");
    }

    @Override
    protected String entryName() {
        return "Redstone and Automation";
    }

    @Override
    protected String entryDescription() {
        return "Mechanical servants for the Ara Vitae -- hoppers, pipes, and comparators.";
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
