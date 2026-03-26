package com.breakinblocks.neovitae.datagen.book.demon_will;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class SoulSnareEntry extends EntryProvider {

    public SoulSnareEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Your First Will");
        this.pageText("[#](8B0000)Soul Snares[#]() are your gateway into the [#](8B0000)Demon Will[#]() portion of Neo Vitae. "
                + "Craft the Soul Snare in the Blood Altar (Tier 1).");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Using the [#](8B0000)Snare[#]() is simple enough - craft a good quantity of them and throw them at "
                + "[#](8B0000)hostile mobs[#](). White particle effects will appear around them; then it's time to go in "
                + "for the kill. On death, they will drop a [#](8B0000)Demon Will[#]().\\\n\\\n"
                + "Before you ask, yes, the Looting "
                + "enchantment will increase the amount of Will dropped. Once you've gathered a couple, you can "
                + "get to work on crafting yourself a [#](8B0000)Sentient Sword[#]() and a [#](8B0000)Tartaric Gem[#]() - these will "
                + "make collecting [#](8B0000)Demon Will[#]() much easier.");

        this.page("image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/demon_will/snare_particles.png"))
                .withTitle("Snare on Skeleton")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("A skeleton with white particles after hit by a snare.");
    }

    @Override
    protected String entryName() {
        return "Your First Will";
    }

    @Override
    protected String entryDescription() {
        return "Using Soul Snares to harvest Demon Will from hostile mobs.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SOUL_SNARE.get());
    }

    @Override
    protected String entryId() {
        return "soul_snare";
    }
}
