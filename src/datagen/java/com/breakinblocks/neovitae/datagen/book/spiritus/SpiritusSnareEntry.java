package com.breakinblocks.neovitae.datagen.book.spiritus;

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

public class SpiritusSnareEntry extends EntryProvider {

    public SpiritusSnareEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The First Harvest");
        this.pageText("[#](8B0000)Soul Snares[#]() are your gateway into the deeper mysteries of [#](4A0080)Spiritus[#](). "
                + "These delicate constructs of thread and intent are forged upon the [#](8B0000)Ara Vitae[#]() at its "
                + "most basic tier.");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("The method is simple enough; prepare a generous supply of [#](8B0000)Snares[#]() and hurl them at "
                + "[#](8B0000)hostile creatures[#](). Pale, spectral motes will gather around the afflicted target; this is "
                + "the moment to strike. Upon death, the creature yields its [#](8B0000)Spiritus[#]().\\\n\\\n"
                + "[#](2E8B57)The Looting enchantment increases the Will dropped.[#]() Once you have collected a few fragments, "
                + "turn your attention to forging a [#](8B0000)Sentient Sword[#]() and a [#](8B0000)Spiritus Gem[#](); these will make "
                + "the harvest far more efficient.");

        this.page("image", () -> BookImagePageModel.create()
                .withImages(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/spiritus/snare_particles.png"))
                .withTitle("Snared Quarry")
                .withBorder(true)
                .withText(this.context().pageText()));
        this.pageText("A skeleton wreathed in spectral motes after being struck by a Snare.");
    }

    @Override
    protected String entryName() {
        return "The First Harvest";
    }

    @Override
    protected String entryDescription() {
        return "Binding spectral thread to pry Spiritus from hostile creatures.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.SPIRITUS_SNARE.get());
    }

    @Override
    protected String entryId() {
        return "spiritus_snare";
    }
}
