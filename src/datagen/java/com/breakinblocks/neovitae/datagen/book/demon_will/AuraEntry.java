package com.breakinblocks.neovitae.datagen.book.demon_will;

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
        this.pageTitle("The Demon Will Aura");
        this.pageText("[#](4A0080)Demonic Will[#]() coalesces naturally around certain creatures, but it can also "
                + "exist in a diffused state throughout the very air. By burning Will in a "
                + "[#](8B0000)Vas Maleficum[#](), you release it into the [#](4A0080)Aura[#]() -- an invisible miasma that "
                + "permeates each chunk of the world, ripe for exploitation.");

        this.page("crucible", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Vas Maleficum");
        this.pageText("Feed it a charged [#](8B0000)Tartaric Gem[#](), loose [#](8B0000)Demon Will[#](), or [#](8B0000)Demon Crystals[#]() "
                + "of any Aspect. Crystals are consumed once the chunk's Will dips below 50; Will in item "
                + "form or from a gem is consumed gradually as needed.");

        this.page("crucible_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/demon_will/vas_maleficum.png"))
                .withTitle("Vas Maleficum")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Vas Maleficum[#]() with a [#](8B0000)Tartaric Gem[#]() smoldering within.");

        this.page("uses", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("With Raw Will now saturating the atmosphere, certain [#](8B0000)Rituals[#]() stir to greater "
                + "potency. The true prize, however, is the ability to grow [#](8B0000)Demon Will Crystals[#]() and "
                + "from there, split them into their constituent [#](8B0000)Aspects[#]().\\\n\\\n"
                + "Once you possess [#](8B0000)Aspected Will Crystals[#](), burn them in the [#](8B0000)Vas Maleficum[#]() to "
                + "flood the Aura with their particular resonance.");

        this.page("chunk_based", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)The Aura is chunk-based.[#]() Will burned in one chunk fills only that chunk, "
                + "up to a cap of [#](B8860B)100[#]() for each Aspect. Measure the local concentration with a "
                + "[#](8B0000)Demon Will Aura Gauge[#]().\\\n\\\n"
                + "To move Will across distances, place a [#](8B0000)Spira Infernalis[#]() in an adjacent chunk. "
                + "It draws Will toward itself, and multiple pylons can be chained for long-distance transfer.");

        this.page("pylon", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spira Infernalis");
        this.pageText("The pylon pulls all varieties of Will from each adjacent chunk (not including "
                + "diagonals). Chain several together to create conduits spanning great distances.");

        this.page("forge_absorption", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Placing a [#](8B0000)Tartaric Gem[#]() inside a [#](8B0000)Hellfire Forge[#]() causes it to rapidly "
                + "siphon Will from the surrounding Aura.\\\n\\\n"
                + "With an Aspected Will present in the chunk and an empty gem in the Forge, you can fill "
                + "that gem with a specific Aspect -- allowing you to reshape the behavior of your "
                + "[#](8B0000)Sentient Tools[#]() and [#](8B0000)Sword[#]() accordingly. The effects of each Aspect are detailed "
                + "in the [#](8B0000)Aspects of Will[#]() entry.");
    }

    @Override
    protected String entryName() {
        return "The Demon Will Aura";
    }

    @Override
    protected String entryDescription() {
        return "Saturating the world with demonic intent through the Vas Maleficum.";
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
