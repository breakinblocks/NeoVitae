package com.breakinblocks.neovitae.datagen.book.altar;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.breakinblocks.neovitae.common.block.NVBlocks;

public class AltarCategory extends CategoryProvider {

    public AltarCategory(ModonomiconProviderBase parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "__________",
                "____a_____",
                "__________"
        };
    }

    @Override
    protected void generateEntries() {
        var bloodAltar = this.add(new BloodAltarEntry(this).generate('a'));
    }

    @Override
    protected String categoryName() {
        return "Blood Altars";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(NVBlocks.BLOOD_ALTAR.asItem());
    }

    @Override
    public String categoryId() {
        return "altar";
    }
}
