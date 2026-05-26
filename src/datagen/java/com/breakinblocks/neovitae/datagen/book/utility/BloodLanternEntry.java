package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.datagen.book.page.BookAraVitaeRecipePageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

public class BloodLanternEntry extends EntryProvider {

    public BloodLanternEntry(CategoryProviderBase parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("intro", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("Blood Lantern");
        this.pageText("The [#](8B0000)Blood Lantern[#]() is a soul lantern reforged within the "
                + "[#](8B0000)Ara Vitae[#](), its pale fire stained crimson by the essence that "
                + "courses through it. It hangs and stands like any lantern, and waterlogs all "
                + "the same.\\\n\\\n"
                + "Where it burns, things that flee the light find no purchase: passive creatures "
                + "and ambient drifters cannot draw breath within sixteen blocks of its flame.");

        this.page("recipe", () -> BookAraVitaeRecipePageModel.create()
                .withRecipeId1(ResourceLocation.fromNamespaceAndPath("neovitae", "ara_vitae/blood_lantern"))
                .withText(this.context().pageText()));
        this.pageText("Place a [#](8B0000)Soul Lantern[#]() in a tier-one Ara Vitae with at least "
                + "1,500 [#](4A0080)Essentia Vitae[#]() and the altar will draw the cold fire out, "
                + "feeding it on warmer stuff.");

        this.page("warding", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText()));
        this.pageTitle("The Quieting Glow");
        this.pageText("Within a sixteen-block radius, [#](8B0000)passive and ambient mobs[#]() "
                + "simply will not spawn. Cows, sheep, bats, fish, axolotls, and the like all "
                + "find the air unwelcoming. [#](2E8B57)Hostile monsters are unaffected[#]() - the "
                + "lantern wards against life, not malice.\\\n\\\n"
                + "Within the [#](8B0000)Dungeon dimension[#]() the warding lies dormant; the "
                + "demonic air there already smothers passive life, and the lantern would only "
                + "waste cycles searching for prey that was never coming.");
    }

    @Override
    protected String entryName() {
        return "Blood Lantern";
    }

    @Override
    protected String entryDescription() {
        return "A crimson-flamed lantern that wards passive and ambient mob spawns.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(NVBlocks.BLOOD_LANTERN.asItem());
    }

    @Override
    protected String entryId() {
        return "blood_lantern";
    }
}
