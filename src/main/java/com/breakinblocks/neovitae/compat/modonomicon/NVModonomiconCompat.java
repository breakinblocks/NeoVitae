package com.breakinblocks.neovitae.compat.modonomicon;

import com.breakinblocks.neovitae.compat.modonomicon.page.*;
import com.klikli_dev.modonomicon.data.LoaderRegistry;

public class NVModonomiconCompat {

    public static void registerPageLoaders() {
        LoaderRegistry.registerPageLoader(
                NVPageTypes.ARA_VITAE,
                BookAraVitaeRecipePage::fromJson,
                BookAraVitaeRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.HELLFIRE_FORGE,
                BookHellfireForgeRecipePage::fromJson,
                BookHellfireForgeRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.TABULA_VITAE,
                BookTabulaVitaeRecipePage::fromJson,
                BookTabulaVitaeRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.ALCHEMY_ARRAY,
                BookAlchemyArrayRecipePage::fromJson,
                BookAlchemyArrayRecipePage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.ATHANOR,
                BookAthanorRecipePage::fromJson,
                BookAthanorRecipePage::fromNetwork
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
        LoaderRegistry.registerPageLoader(
                NVPageTypes.BLOOD_ORB_STATS,
                BookBloodOrbStatsPage::fromJson,
                BookBloodOrbStatsPage::fromNetwork
        );
        LoaderRegistry.registerPageLoader(
                NVPageTypes.SPIRITUS_GEM_STATS,
                BookSpiritusGemStatsPage::fromJson,
                BookSpiritusGemStatsPage::fromNetwork
        );
    }
}
