package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookMultiblockPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import net.minecraft.resources.ResourceLocation;
import com.mojang.datafixers.util.Pair;

public class BloodAltarEntry extends EntryProvider {

    public BloodAltarEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        // Intro page
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Blood Altar");
        this.pageText("The Blood Altar is the central crafting station of Neo Vitae. "
                + "It converts Life Points into powerful items and serves as the foundation "
                + "for increasingly complex multiblock structures.\n\n"
                + "Each tier unlocks new crafting recipes and increases the altar's capacity and power.");

        // Tier 1
        this.page("tier1", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_one"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 1");
        this.pageText("The base altar. Place it down and right-click with a Sacrificial Dagger to begin.");

        // Tier 2
        this.page("tier2", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_two"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 2");
        this.pageText("Surround the altar with 8 Blood Runes one block below. Any rune type works for the structure.");

        // Tier 3
        this.page("tier3", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_three"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 3");
        this.pageText("Add an outer ring of runes, four pillars of any solid block, and glowstone caps on each pillar.");

        // Tier 4
        this.page("tier4", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_four"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 4");
        this.pageText("Extend the pillars upward with Bloodstone caps. Add a larger outer ring of runes at a lower level.");

        // Tier 5
        this.page("tier5", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_five"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 5");
        this.pageText("The largest rune ring yet, with Hellforged Blocks at the four corners.");

        // Tier 6
        this.page("tier6", () -> BookMultiblockPageModel.create()
                .withMultiblockId(ResourceLocation.fromNamespaceAndPath(NeoVitae.MODID, "altar_six"))
                .withMultiblockName(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Tier 6");
        this.pageText("The ultimate altar. Four corner pillars topped with Crystal Clusters, "
                + "and the largest rune ring spanning 23 blocks across.");
    }

    @Override
    protected String entryName() {
        return "Blood Altar";
    }

    @Override
    protected String entryDescription() {
        return "The central crafting station and its multiblock tiers.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.BLOOD_ALTAR.asItem());
    }

    @Override
    protected String entryId() {
        return "blood_altar";
    }
}
