package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import com.mojang.datafixers.util.Pair;

public class RitualPhantomBridgeEntry extends EntryProvider {

    public RitualPhantomBridgeEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/phantom_bridge"))
                .withMultiblockName("Ritual of the Phantom Bridge")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("phantom_bridge")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Spectral Pathways");
        this.pageText("This ritual weaves [#](4A0080)phantom matter[#]() beneath the feet of any practitioner within its reach, translucent platforms that solidify only when stepped upon, allowing passage across chasms and voids.\\\n\\\n"
                + "When the ritual falls silent, the phantom bridges dissolve like morning mist. Invaluable for traversing dangerous terrain or constructing in treacherous places.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Phantom Bridge";
    }

    @Override
    protected String entryDescription() {
        return "Weaves spectral platforms beneath your feet.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.GLASS);
    }

    @Override
    protected String entryId() {
        return "ritual_phantom_bridge";
    }
}
