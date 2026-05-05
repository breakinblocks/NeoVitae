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
import net.minecraft.resources.Identifier;

public class SpiritusEntry extends EntryProvider {

    public SpiritusEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spiritus");
        this.pageText("Every creature that walks, crawls, or slithers through the dark carries within it a shard of "
                + "[#](4A0080)demonic intent[#](), a residue left when entities of the lower planes imbue their malice "
                + "into mortal flesh. This essence is known as [#](8B0000)Spiritus[#](), and it is yours to harvest.\\\n\\\n"
                + "There are two methods of extraction:\n\n"
                + "- Strike a hostile creature with a [#](8B0000)Throwing Dagger[#](); the wound binds spectral motes to its form, and slaying it while marked yields its Spiritus.\n\n"
                + "- Fell it outright with a [#](8B0000)Sentient Sword[#]().");

        this.page("usage", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("As a fledgling Vitaemancer, you will not yet possess a [#](8B0000)Sentient Sword[#](), so the "
                + "[#](8B0000)Throwing Dagger[#]() shall serve as your first instrument of collection.\\\n\\\n"
                + "[#](8B0000)Spiritus[#]() is a cornerstone of [#](4A0080)Vitaemancy[#](), fueling the [#](8B0000)Hellfire Forge[#]() "
                + "and the creation of ever more potent artifacts. Where [#](4A0080)Essentia Vitae[#]() is the currency "
                + "of the blood, [#](4A0080)Spiritus[#]() is the currency of the soul.");

        this.page("image", () -> BookImagePageModel.create()
                .withImages(Identifier.fromNamespaceAndPath(NeoVitae.MODID, "images/entries/spiritus/spiritus.png"))
                .withTitle("Spiritus")
                .withBorder(true));

        this.page("next_steps", () -> BookTextPageModel.create()
                .withText(this.context().pageText()));
        this.pageText("Once you have gathered some Spiritus, the [#](8B0000)Hellfire Forge[#]() awaits, eager to transmute "
                + "this raw malice into instruments of power. Should loose fragments begin to clutter your person, "
                + "a [#](8B0000)Spiritus Gem[#]() will serve as a most convenient receptacle.");
    }

    @Override
    protected String entryName() {
        return "Spiritus";
    }

    @Override
    protected String entryDescription() {
        return "The malevolent essence bound within all mortal creatures.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.MONSTER_SOUL_RAW.get());
    }

    @Override
    protected String entryId() {
        return "spiritus";
    }
}
