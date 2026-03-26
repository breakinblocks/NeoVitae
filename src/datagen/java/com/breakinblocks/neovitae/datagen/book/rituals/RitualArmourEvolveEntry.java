package com.breakinblocks.neovitae.datagen.book.rituals;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.item.NVItems;
import com.breakinblocks.neovitae.datagen.book.page.BookRitualInfoPageModel;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class RitualArmourEvolveEntry extends EntryProvider {

    public RitualArmourEvolveEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/armour_evolve"))
                .withMultiblockName("Ritual of Living Evolution")
                .withText(this.context().pageText()));
        this.pageText("Use a [#](8B0000)Ritual Diviner [Dusk][#]() for easier construction.");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withTitle("Ritual Stats")
                .withText(RitualStatsHelper.generateStats("armour_evolve")));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Ritual Info");
        this.pageText("Stand on the [#](8B0000)Master Ritual Stone[#]() while wearing [#](8B0000)Living Armor[#]() to use this ritual. The ritual will evolve your Living Armor, unlocking additional upgrade potential and capabilities.");
    }

    @Override
    protected String entryName() {
        return "Ritual of Living Evolution";
    }

    @Override
    protected String entryDescription() {
        return "Evolves Living Armor to unlock potential.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.LIVING_PLATE.get());
    }

    @Override
    protected String entryId() {
        return "ritual_armour_evolve";
    }
}
