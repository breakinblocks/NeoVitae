package com.breakinblocks.neovitae.compat.modonomicon;

import com.breakinblocks.neovitae.compat.modonomicon.page.*;
import com.klikli_dev.modonomicon.data.BookPageType;

public class NVModonomiconCompat {

    public static void registerPageLoaders() {
        BookPageType<?>[] types = {
                BookAraVitaeRecipePage.TYPE,
                BookHellfireForgeRecipePage.TYPE,
                BookTabulaVitaeRecipePage.TYPE,
                BookAlchemyArrayRecipePage.TYPE,
                BookAthanorRecipePage.TYPE,
                BookFlaskRecipePage.TYPE,
                BookSentientDowngradeRecipePage.TYPE,
                BookRitualInfoPage.TYPE,
                BookSentientUpgradeTablePage.TYPE,
                BookBloodOrbStatsPage.TYPE,
                BookSpiritusGemStatsPage.TYPE
        };
    }
}
