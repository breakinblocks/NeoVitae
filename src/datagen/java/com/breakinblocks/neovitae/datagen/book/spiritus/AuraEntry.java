package com.breakinblocks.neovitae.datagen.book.spiritus;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class AuraEntry extends EntryProvider {

    public AuraEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Spiritus Aura");
        this.pageText("[#](4A0080)Demonic Spiritus[#]() coalesces naturally around certain creatures, but it can also "
                + "exist in a diffused state throughout the very air. By burning Spiritus in a "
                + "[#](8B0000)Spiritus Crucible[#](), you release it into the [#](4A0080)Aura[#](), an invisible miasma that "
                + "permeates each chunk of the world, ripe for exploitation.");

        this.page("crucible", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Crucible");
        this.pageText("Feed it a charged [#](8B0000)Spiritus Gem[#](), loose [#](8B0000)Spiritus[#](), or [#](8B0000)Spiritus Crystals[#]() "
                + "of any Aspect. Crystals are consumed once the chunk's Spiritus dips below 50; Spiritus in item "
                + "form or from a gem is consumed gradually as needed.\\\n\\\n"
                + "[#](2E8B57)Apply a redstone signal[#]() and the Vas reverses: a Spiritus Gem placed inside is now "
                + "[#](2E8B57)filled[#]() from the chunk's Aura instead of drained. The same vessel serves as both "
                + "deposit and withdrawal point.");

        this.page("crucible_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/spiritus/vas_maleficum.png"))
                .withTitle("Spiritus Crucible")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Spiritus Crucible[#]() with a [#](8B0000)Spiritus Gem[#]() smoldering within.");

        this.page("uses", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("With Raw Spiritus now saturating the atmosphere, certain [#](8B0000)Rituals[#]() stir to greater "
                + "potency. The true prize, however, is the ability to grow [#](8B0000)Spiritus Crystals[#]() and "
                + "from there, split them into their constituent [#](8B0000)Aspects[#]().\\\n\\\n"
                + "Once you possess [#](8B0000)Aspected Spiritus Crystals[#](), burn them in the [#](8B0000)Spiritus Crucible[#]() to "
                + "flood the Aura with their particular resonance.");

        this.page("chunk_based", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)The Aura is chunk-based.[#]() Spiritus burned in one chunk fills only that chunk, "
                + "up to a cap of [#](B8860B)100[#]() for each Aspect. Measure the local concentration with a "
                + "[#](8B0000)Spiritus Aura Gauge[#]().\\\n\\\n"
                + "To move Spiritus across distances, place a [#](8B0000)Spiritus Conduit[#](). It draws Spiritus "
                + "toward itself from 16 blocks away in each cardinal direction, and multiple pylons can be "
                + "chained for long-distance transfer.");

        this.page("pylon", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus Conduit");
        this.pageText("The pylon probes a position [#](B8860B)16 blocks away[#]() in each of the four cardinal "
                + "directions (no diagonals, no vertical). If a probed position holds more Spiritus than the "
                + "pylon's own chunk does, the pylon pulls a small fraction of the difference per tick. The flow "
                + "equilibrates rather than draining the source dry, so place pylons in a [#](2E8B57)chain[#]() "
                + "from a saturated chunk toward your worksite to ferry Aura over long distances.");

        this.page("gem_absorption", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("This reversal is how an aspected gem is made. Saturate a chunk with a single Aspect, "
                + "leave an empty [#](8B0000)Spiritus Gem[#]() in a redstone-charged [#](8B0000)Spiritus Crucible[#]() there, "
                + "and it takes on that Aspect as it fills.\\\n\\\n"
                + "An aspected gem reshapes the behavior of your [#](8B0000)Sentient Tools[#]() and [#](8B0000)Sword[#](). "
                + "The effects of each Aspect are detailed in the [#](8B0000)Aspects of Spiritus[#]() entry.");
    }

    @Override
    protected String entryName() {
        return "The Spiritus Aura";
    }

    @Override
    protected String entryDescription() {
        return "Saturating the world with demonic intent through the Spiritus Crucible.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.VAS_MALEFICUM.asItem());
    }

    @Override
    protected String entryId() {
        return "aura";
    }
}
