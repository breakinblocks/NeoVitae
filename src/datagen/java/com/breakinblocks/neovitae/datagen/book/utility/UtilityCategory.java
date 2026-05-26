package com.breakinblocks.neovitae.datagen.book.utility;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.block.NVBlocks;

public class UtilityCategory extends CategoryProvider {

    public UtilityCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        // Organized: reference at top, crafting stations middle, materials/items bottom
        return new String[]{
                "____g__________",
                "__s___e________",
                "____a__________",
                "__h___o___i_n__",
                "__b___l___r____",
                "____t__________"
        };
    }

    @Override
    protected void generateEntries() {
        var gettingStarted = this.add(new GettingStartedEntry(this).generate('g'));
        var glossary = this.add(new GlossaryEntry(this).generate('s'));
        var bookExperience = this.add(new BookExperienceEntry(this).generate('e'));
        var incenseAltar = this.add(new IncenseAltarEntry(this).generate('i'));
        var hydration = this.add(new HydrationEntry(this).generate('h'));
        var arc = this.add(new AthanorEntry(this).generate('a'));
        var oreProcessing = this.add(new OreProcessingEntry(this).generate('o'));
        var bloodstoneBricks = this.add(new BloodstoneBricksEntry(this).generate('b'));
        var lavaCrystal = this.add(new LavaCrystalEntry(this).generate('l'));
        var essentiaVitaeBucket = this.add(new EssentiaVitaeBucketEntry(this).generate('r'));
        var teleposer = this.add(new TeleposerEntry(this).generate('t'));
        var bloodLantern = this.add(new BloodLanternEntry(this).generate('n'));
    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return super.additionalSetup(category)
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/utility_base.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/utility_layer_1.png"))
                .withBackgroundParallaxLayer(NeoVitae.rl("textures/gui/parallax/utility_layer_2.png"));
    }

    @Override
    protected String categoryName() {
        return "Utility Blocks & Items";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVBlocks.INCENSE_ALTAR.asItem());
    }

    @Override
    public String categoryId() {
        return "utility";
    }

}
