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
        this.pageTitle("Demon Will Aura");
        this.pageText("As we have established, [#](8B0000)Demonic Will[#]() coalesces around certain creatures and propels "
                + "them with malevolent force. However, this is not the only place that Will can exist. By "
                + "burning Will in a [#](8B0000)Demon Crucible[#](), it is possible to unleash the will into the [#](8B0000)Aura[#](), "
                + "to great and fascinating effect.");

        this.page("crucible", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Crucible");
        this.pageText("Craft the Demon Crucible in the Hellfire Forge.\\\n\\\n"
                + "Put a charged [#](8B0000)Tartaric Gem[#]() or any aspect of [#](8B0000)Demon Will[#]() or [#](8B0000)Demon Crystal[#]() into "
                + "it and let it run. [#](8B0000)Crystals[#]() will be consumed once the chunk's Will dips below 50, "
                + "whereas Demon Will (in item form or from the Tartaric Gem) will be consumed a bit at a "
                + "time, as needed.");

        this.page("crucible_image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/demon_will/demon_crucible.png"))
                .withTitle("Demon Crucible")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("The [#](8B0000)Demon Crucible[#](), with a [#](8B0000)Tartaric Gem[#]() inside it.");

        this.page("uses", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Now we have Raw Will in the atmosphere. Great, now what?\\\n\\\n"
                + "Some [#](8B0000)Rituals[#]() benefit from Raw Will, but the main benefit from this is the ability to "
                + "create refined [#](8B0000)Demon Will Crystals[#]() and from there, split them into their [#](8B0000)Aspects[#]().\\\n\\\n"
                + "Once you have some of these [#](8B0000)Aspected Will Crystals[#](), you can burn them once more in the "
                + "[#](8B0000)Crucible[#]() to unleash them into the [#](8B0000)Aura[#]() for your rituals to benefit from.");

        this.page("chunk_based", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("It's important to mention that the [#](8B0000)Aura[#]() is chunk-based. That is, any Will you burn "
                + "will fill up the chunk that you burn it in, up to a total cap of 100 for each type. You can "
                + "measure this using a [#](8B0000)Demon Will Aura Gauge[#]().\\\n\\\n"
                + "To move Demon Will around, simply place a [#](8B0000)Demon Pylon[#]() in any adjacent chunk, and Will "
                + "will be drawn towards it.");

        this.page("pylon", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Demon Pylon");
        this.pageText("Craft the Demon Pylon in the Hellfire Forge.\\\n\\\n"
                + "The Pylon will draw in all kinds of will from all adjacent chunks (not including diagonals). "
                + "Multiple Pylons can be chained in order to transfer Will over larger distances.");

        this.page("forge_absorption", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Placing a [#](8B0000)Tartaric Gem[#]() inside a [#](8B0000)Hellfire Forge[#]() will cause it to rapidly absorb "
                + "Will from the chunk it's in.\\\n\\\n"
                + "With an Aspect of Will in the chunk, such as Raw or Corrosive, and an empty [#](8B0000)Tartaric Gem[#](), "
                + "it's possible to fill the Gem with that Aspect.\\\n\\\n"
                + "This lets you modify your [#](8B0000)Sentient Tools[#]() and [#](8B0000)Sword[#]() accordingly. The effects are "
                + "defined in the [#](8B0000)Demon Will Aspects[#]() entry.");
    }

    @Override
    protected String entryName() {
        return "Demon Will Aura";
    }

    @Override
    protected String entryDescription() {
        return "Releasing Demon Will into the world's aura.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.DEMON_CRUCIBLE.asItem());
    }

    @Override
    protected String entryId() {
        return "aura";
    }
}
