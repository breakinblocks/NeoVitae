package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualSphereCreateEntry extends EntryProvider {

    public RitualSphereCreateEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "ritual/sphere"))
                .withMultiblockName("Dawn of the New Moon")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("sphere")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Lifted Earth");
        this.pageText("Few rituals are as theatrical as this. The circle scoops a [#](8B0000)solid ellipsoidal "
                + "volume[#]() of earth from directly beneath the [#](8B0000)Master Ritual Stone[#]() and teleports "
                + "it skyward, blocks rising one by one until a [#](4A0080)floating moon of earth and stone[#]() "
                + "hangs above the ritual where the ground used to be. The void left behind exactly mirrors the "
                + "moon's shape, a bowl of empty air carved into the world.\\\n\\\n"
                + "Each lifted block drains [#](8B0000)10 EV[#](). The ritual processes up to one hundred block "
                + "checks per refresh and remembers where it left off, so even the largest moons assemble "
                + "incrementally across many seconds of operation. The ritual auto-deactivates once the full "
                + "volume has been swept. Claim-protected ground is left untouched, and the moon's destination "
                + "skips blocks that fall within someone else's claim.");

        this.page("foundation", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Foundation Stone");
        this.pageText("The size of the lifted moon is set by the [#](8B0000)block placed directly beneath the "
                + "Master Ritual Stone[#](). The richer the foundation, the larger the moon:\\\n\\\n"
                + "- [#](8B0000)Any other block[#](): 33 blocks across\\\n"
                + "- [#](8B0000)Iron Block[#](): 41 blocks across\\\n"
                + "- [#](8B0000)Gold Block[#](): 49 blocks across\\\n"
                + "- [#](8B0000)Diamond Block[#](): 57 blocks across\\\n"
                + "- [#](8B0000)Netherite Block[#](): [#](2E8B57)65 blocks across[#]()\\\n\\\n"
                + "The moon's source volume begins two blocks below the master and descends straight down for "
                + "the full diameter; the destination begins two blocks above and rises the same distance. Mind "
                + "what is in either space before activation.");
    }

    @Override
    protected String entryName() {
        return "Dawn of the New Moon";
    }

    @Override
    protected String entryDescription() {
        return "Lifts a moon-sized volume of terrain out of the ground beneath the ritual.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.ENDER_PEARL);
    }

    @Override
    protected String entryId() {
        return "ritual_sphere_create";
    }
}
