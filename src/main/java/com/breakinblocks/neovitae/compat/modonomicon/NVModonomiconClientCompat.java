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
                p -> new BookSoulForgeRecipePageRenderer((BookSoulForgeRecipePage) p)
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
                NVPageTypes.ARC,
                p -> new BookARCRecipePageRenderer((BookARCRecipePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.FLASK,
                p -> new BookFlaskRecipePageRenderer((BookFlaskRecipePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.LIVING_DOWNGRADE,
                p -> new BookLivingDowngradeRecipePageRenderer((BookLivingDowngradeRecipePage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.RITUAL_INFO,
                p -> new BookRitualInfoPageRenderer((BookRitualInfoPage) p)
        );
        PageRendererRegistry.registerPageRenderer(
                NVPageTypes.LIVING_UPGRADE_TABLE,
                p -> new BookLivingUpgradeTablePageRenderer((BookLivingUpgradeTablePage) p)
        );
    }
}
