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

public class RitualCrystallumFracturaEntry extends EntryProvider {

    public RitualCrystallumFracturaEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("multiblock", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "ritual/crystallum_fractura"))
                .withMultiblockName("Ritual of Crystallum Fractura")
                .withText(this.context().pageText()));
        this.pageText("[#](2E8B57)Use a Ritual Diviner [Dusk] for easier construction.[#]()");

        this.page("stats", () -> BookRitualInfoPageModel.create()
                .withText(RitualStatsHelper.generateStats(com.breakinblocks.neovitae.ritual.types.RitualCrystallumFractura.NAME)));

        this.page("info", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Crystallum Fractura");
        this.pageText("A unified harvest engine that gathers fully-grown [#](8B0000)Spiritus Crystals[#]() and any cluster tagged [#](2E8B57)neovitae:geode_harvestable[#](). Within its aura, crystal growth doubles, and any spiritus injected into a chunk is amplified by [#](B8860B)+25%%[#]().");

        this.page("fortune", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Fracture's Edge");
        this.pageText("Harvested clusters drop with [#](8B0000)scaling Fortune[#]() based on the chunk's local Raw Spiritus density:"
                + "\n\n- below [#](B8860B)30 Raw[#](): no Fortune"
                + "\n- [#](B8860B)30-100 Raw[#](): linearly scales from Fortune I to Fortune III"
                + "\n- at or above [#](B8860B)100 Raw[#](): Fortune III"
                + "\n\nWhen Fortune is active, the ritual probabilistically consumes [#](8B0000)Raw Spiritus[#]() at an average rate of one per twelve seconds.");

        this.page("aspect_bias", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Aspect Bias");
        this.pageText("Attune the Master Ritual Stone to a single Spiritus aspect with the [#](8B0000)Ritual Tinkerer[#]() (one matching crystal in the hotbar). The +25%% portion of any [#](8B0000)Raw Spiritus[#]() injection within the aura is then redirected to that aspect's pool, letting you farm a chosen aspect while the ritual is running.");
    }

    @Override
    protected String entryName() {
        return "Crystallum Fractura";
    }

    @Override
    protected String entryDescription() {
        return "Harvests mature crystal clusters and weaves a growth aura around them.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVItems.RAW_SPIRITUS_CRYSTAL_ITEM.get());
    }

    @Override
    protected String entryId() {
        return "ritual_crystallum_fractura";
    }
}
