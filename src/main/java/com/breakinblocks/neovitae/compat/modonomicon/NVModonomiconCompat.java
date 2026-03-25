package com.breakinblocks.neovitae.compat.modonomicon;

import com.breakinblocks.neovitae.compat.modonomicon.page.*;
import com.klikli_dev.modonomicon.data.LoaderRegistry;

public class NVModonomiconCompat {

    public static void registerPageLoaders() {
        LoaderRegistry.registerPageLoader(
                NVPageTypes.BLOOD_ALTAR,
                BookBloodAltarRecipePage::fromJson,
                BookBloodAltarRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.SOUL_FORGE,
                BookSoulForgeRecipePage::fromJson,
                BookSoulForgeRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.ALCHEMY_TABLE,
                BookAlchemyTableRecipePage::fromJson,
                BookAlchemyTableRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.ALCHEMY_ARRAY,
                BookAlchemyArrayRecipePage::fromJson,
                BookAlchemyArrayRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.ARC,
                BookARCRecipePage::fromJson,
                BookARCRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.FLASK,
                BookFlaskRecipePage::fromJson,
                BookFlaskRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.LIVING_DOWNGRADE,
                BookLivingDowngradeRecipePage::fromJson,
                BookLivingDowngradeRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.RITUAL_INFO,
                BookRitualInfoPage::fromJson,
                BookRitualInfoPage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.LIVING_UPGRADE_TABLE,
                BookLivingUpgradeTablePage::fromJson,
                BookLivingUpgradeTablePage::fromNetwork
        );
    }
}
