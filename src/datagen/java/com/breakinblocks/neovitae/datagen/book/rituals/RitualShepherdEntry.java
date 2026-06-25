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

public class RitualShepherdEntry extends EntryProvider {

    public RitualShepherdEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/shepherd"))
                .withMultiblockName("Ritual of the Shepherd")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Built from standard runes; any Ritual Diviner will serve.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("shepherd")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Tending Circle");
        this.pageText("The circle keeps a watchful eye over the flock within its bounds. Young creatures grow to adulthood at a quickened pace, while grown animals are coaxed into breeding of their own accord. A trickle of [#](8B0000)Essentia Vitae[#]() feeds them in place of grain, so neither food nor a chest is required. A chunk rich in [#](8B0000)Raw Spiritus[#]() quickens the circle's pulse.");
    }

    @Override
    protected String entryName() {
        return "Ritual of the Shepherd";
    }

    @Override
    protected String entryDescription() {
        return "Tends a flock: hastens the young and breeds the grown.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.WHITE_WOOL);
    }

    @Override
    protected String entryId() {
        return "ritual_shepherd";
    }
}
