package com.breakinblocks.neovitae.datagen.book.alchemy_array;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import com.breakinblocks.neovitae.common.item.NVItems;

public class VortexSigilEntry extends EntryProvider {

    public VortexSigilEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Vortex Sigil");
        this.pageText("A drawing array, the [#](8B0000)Vortex Sigil[#]() pulls all living things toward the block "
                + "directly beneath its inscription. The grip is fierce, comparable to a rocket-boosted elytra; "
                + "few creatures can resist it once within its eight-block reach.\\\n\\\n"
                + "Inscribed from a [#](8B0000)Tabula Robur[#]() and a [#](8B0000)Blood Pearl[#]() upon a layer of "
                + "Arcane Ash.");

        this.page("upkeep", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Sustained by Anima");
        this.pageText("The vortex feeds on the practitioner who bound it. Each tick of operation has a roughly "
                + "[#](8B0000)1-in-100[#]() chance to draw a single [#](8B0000)EV[#]() from your Anima network. "
                + "Across a full second of activity this averages out to about one fifth of an EV; a negligible "
                + "cost over short sessions, but the toll accumulates if the sigil is left running for hours on "
                + "end.\\\n\\\n"
                + "If the bound network has nothing left to give, the vortex skips that tick - no pull, no "
                + "particles - and tries again the next. The sigil remains inert in the world, waiting "
                + "patiently for the well to refill.");

        this.page("immunities", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("What the Vortex Cannot Hold");
        this.pageText("Several beings are beyond the sigil's grasp:\\\n\\\n"
                + "- [#](2E8B57)Players in Creative or Spectator mode[#]() pass through unaffected, as expected.\\\n"
                + "- [#](2E8B57)Any practitioner holding an Orb of Vitae[#]() - of any tier, in either main hand "
                + "or off-hand - is recognised by the array and ignored. The orb's aura disrupts the drawing "
                + "current. Useful for working near your own vortex without being yanked into it.\\\n"
                + "- A [#](2E8B57)redstone signal[#]() applied to the sigil silences it entirely; nothing within "
                + "the radius is pulled while the signal is active.");
    }

    @Override
    protected String entryName() {
        return "Vortex Sigil";
    }

    @Override
    protected String entryDescription() {
        return "A drawing array that yanks all nearby living things toward its centre.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.BLOOD_PEARL.get());
    }

    @Override
    protected String entryId() {
        return "vortex_sigil";
    }
}
