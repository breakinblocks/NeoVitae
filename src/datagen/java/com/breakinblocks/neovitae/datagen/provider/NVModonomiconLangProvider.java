package com.breakinblocks.neovitae.datagen.provider;

import com.klikli_dev.modonomicon.api.datagen.AbstractModonomiconLanguageProvider;
import net.minecraft.data.PackOutput;
import com.breakinblocks.neovitae.NeoVitae;

public class NVModonomiconLangProvider extends AbstractModonomiconLanguageProvider {

    public NVModonomiconLangProvider(PackOutput output) {
        super(output, NeoVitae.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Book translations are added automatically by the BookProvider via this BiConsumer.
        // Add any additional non-book Modonomicon translations here if needed.
    }
}
