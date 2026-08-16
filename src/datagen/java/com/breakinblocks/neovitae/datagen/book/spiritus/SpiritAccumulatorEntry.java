package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.klikli_dev.modonomicon.client.gui.book.theme.GuiSprite;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.datagen.book.page.BookAthanorRecipePageModel;
import net.minecraft.resources.Identifier;

public class SpiritAccumulatorEntry extends EntryProvider {

    public SpiritAccumulatorEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spirit Accumulator");
        this.pageText("The [#](4A0080)Aura[#]() will hold no more than [#](8B0000)100[#]() of any one Aspect, and it "
                + "cannot leave the chunk that holds it. Every crystal you burn past that ceiling is simply "
                + "lost.\\\n\\\n"
                + "The [#](8B0000)Spirit Accumulator[#]() is the answer: a suspended crystal that gathers "
                + "[#](8B0000)1,000[#]() Spiritus of a single Aspect and surrenders it to your "
                + "[#](4A0080)Routing Network[#]() wherever it is wanted.");

        this.page("cost", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("What It Asks");
        this.pageText("Beyond the reagents, the chunk in which the Athanor labors must hold "
                + "[#](8B0000)45 Raw[#]() Spiritus. A single Raw [#](8B0000)shard[#]() burned in a "
                + "[#](8B0000)Vas Maleficum[#]() settles that on its own, for a shard gives 50 whenever its "
                + "Aspect stands below 50.\\\n\\\n"
                + "[#](2E8B57)An Animus Mote is required too, and those are found only in the deep "
                + "vaults of the Demon Realm.[#]()");

        this.page("recipe", () -> BookAthanorRecipePageModel.create()
                .withRecipeId1(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "athanor/resonator/spirit_accumulator")));

        this.page("attunement", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Attunement");
        this.pageText("A newly placed Accumulator hangs dark and unattuned. It gathers nothing at all until "
                + "you have told it what to seek.\\\n\\\n"
                + "[#](8B0000)Right-click[#]() it to cycle the Aspect: Raw, Ruina, Nihilum, Invictus, "
                + "Vindicta, and dark again. [#](8B0000)Crouch and right-click[#]() to lock the choice. Only "
                + "once locked does it begin to drink.\\\n\\\n"
                + "[#](8B0000)Right-click with a Spiritus crystal[#]() of any Aspect and it takes that Aspect "
                + "at once and locks. The crystal is not consumed.");

        this.page("retuning", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Retuning");
        this.pageText("A locked Accumulator refuses every other Aspect. To change its allegiance you must "
                + "empty it first: [#](8B0000)crouch and right-click[#]() to vent [#](8B0000)50[#]() back "
                + "into the surrounding chunk.\\\n\\\n"
                + "Once it stands empty, the next crouch-click releases the lock and the Aspect may be "
                + "cycled again.\\\n\\\n"
                + "[#](2E8B57)Drawing it dry through the network does not release the lock. A locked buffer "
                + "keeps gathering its chosen Aspect no matter how hard the network leans on it.[#]()\\\n\\\n"
                + "Break one and nothing is spilled: the crystal carries its Aspect and its whole reserve in "
                + "your hand, and surrenders them again wherever you set it down.");

        this.page("skimming", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Skimming the Aura");
        this.pageText("Once locked, the Accumulator drinks from the chunk it stands in: up to "
                + "[#](8B0000)25[#]() Spiritus each tick, but only from the surplus above "
                + "[#](8B0000)30[#](). It will never draw the chunk below that mark.\\\n\\\n"
                + "That floor sits low enough that a single crystal burned in a "
                + "[#](8B0000)Vas Maleficum[#]() will feed it, for a crystal lifts its Aspect to "
                + "[#](8B0000)50[#](). Set one in a saturated crystal farm instead and it becomes an "
                + "overflow vessel, catching everything burned past the ceiling.\\\n\\\n"
                + "[#](2E8B57)The crystal's inner light shows its Aspect and how full it stands; it burns "
                + "dim while unlocked.[#]()");

        this.page("network", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Joining the Network");
        this.pageText("An Accumulator is itself a [#](8B0000)routing node[#](). Place it within "
                + "[#](8B0000)16 blocks[#]() of a network and it binds on its own, exactly as an "
                + "[#](8B0000)Input Node[#]() would; the [#](8B0000)Node Router[#]() links it by hand when "
                + "auto-binding will not reach. Its thread springs from the very tip of the crystal.\\\n\\\n"
                + "Whatever it holds is then offered to the whole network. It gives only; the chunk beneath "
                + "it is the one thing that fills it.");

        this.page("export", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Seeding Distant Chunks");
        this.pageText("To spend the reserve, open any [#](8B0000)Output Routing Node[#]() and turn its tab to "
                + "[#](8B0000)Spiritus[#](). Two controls await:\\\n\\\n"
                + "- [#](8B0000)Type[#](): cycles through Off and the five Aspects.\\\n"
                + "- [#](8B0000)Keep[#](): how much of that Aspect to maintain in the node's own chunk, in "
                + "steps of 10, or 100 with [#](8B0000)Shift[#]() held.\\\n\\\n"
                + "The node holds its chunk at that figure and no higher, and never forces the Aura past its "
                + "natural ceiling. No attached block is needed; an Output Node given to Spiritus may stand "
                + "alone in the middle of the ground it feeds.");

        this.page("practice", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("In Practice");
        this.pageText("One Accumulator on the crystal farm and one Output Node in each chunk that hungers: "
                + "a ritual site, an Athanor, a Crystallarium seeding its first spire. The network holds "
                + "them all at the level you name, across any distance it spans.\\\n\\\n"
                + "Run one Accumulator per Aspect you farm; a single network may hold as many as you like, "
                + "and each Output Node draws only from those matching the Aspect it names.\\\n\\\n"
                + "[#](8B0000)Beware:[#]() an Accumulator sharing a chunk with an Output Node of the same "
                + "Aspect will skim back whatever that node delivers above 30. Keep them in separate chunks, "
                + "or set the node's Keep figure to 30 or lower.");
    }

    @Override
    protected String entryName() {
        return "Spirit Accumulator";
    }

    @Override
    protected String entryDescription() {
        return "A reservoir that banks the Aura and feeds it to the network.";
    }

    @Override
    protected GuiSprite entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.SPIRIT_ACCUMULATOR.asItem());
    }

    @Override
    protected String entryId() {
        return "spirit_accumulator";
    }
}
