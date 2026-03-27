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

public class RitualSuppressionEntry extends EntryProvider {

    public RitualSuppressionEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/suppression"))
                .withMultiblockName("Dome of Suppression")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats("suppression")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Parted Tide");
        this.pageText("This ritual projects a hemispherical ward that holds all fluids at bay; water, lava, and stranger liquids alike are temporarily suppressed, not destroyed. When the circle falls silent, the tide returns as though nothing happened.\\\n\\\n"
                + "[#](2E8B57)Invaluable for underwater construction, draining lava lakes, or carving safe pockets in flooded depths.[#]()");
    }

    @Override
    protected String entryName() {
        return "Dome of Suppression";
    }

    @Override
    protected String entryDescription() {
        return "Holds all fluids at bay within a warded dome.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.SPONGE);
    }

    @Override
    protected String entryId() {
        return "ritual_suppression";
    }
}
