package com.breakinblocks.neovitae.compat.modonomicon;

import com.breakinblocks.neovitae.compat.modonomicon.page.*;
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry;

public class NVModonomiconClientCompat {

    public static void registerPageRenderers() {
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.ARA_VITAE,
                p -> new BookAraVitaeRecipePageRenderer((BookAraVitaeRecipePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.HELLFIRE_FORGE,
                p -> new BookHellfireForgeRecipePageRenderer((BookHellfireForgeRecipePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.TABULA_VITAE,
                p -> new BookTabulaVitaeRecipePageRenderer((BookTabulaVitaeRecipePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.ALCHEMY_ARRAY,
                p -> new BookAlchemyArrayRecipePageRenderer((BookAlchemyArrayRecipePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.ATHANOR,
                p -> new BookAthanorRecipePageRenderer((BookAthanorRecipePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.FLASK,
                p -> new BookFlaskRecipePageRenderer((BookFlaskRecipePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.SENTIENT_DOWNGRADE,
                p -> new BookSentientDowngradeRecipePageRenderer((BookSentientDowngradeRecipePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.RITUAL_INFO,
                p -> new BookRitualInfoPageRenderer((BookRitualInfoPage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.SENTIENT_UPGRADE_TABLE,
                p -> new BookSentientUpgradeTablePageRenderer((BookSentientUpgradeTablePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.BLOOD_ORB_STATS,
                p -> new BookBloodOrbStatsPageRenderer((BookBloodOrbStatsPage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.SPIRITUS_GEM_STATS,
                p -> new BookSpiritusGemStatsPageRenderer((BookSpiritusGemStatsPage) p)
        );
    }
}
