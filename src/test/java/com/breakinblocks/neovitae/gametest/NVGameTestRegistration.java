package com.breakinblocks.neovitae.gametest;

import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

public class NVGameTestRegistration {

    public static void registerTests(RegisterGameTestsEvent event) {
        NVTestRegistrar r = new NVTestRegistrar(event);

        AlchemyArrayTests.register(r);
        AnimaTests.register(r);
        AnointmentTests.register(r);
        AraVitaeTests.register(r);
        VitaeLinkTests.register(r);
        OrbVitaeLinkTests.register(r);
        AthanorTests.register(r);
        AthanorSideTests.register(r);
        BloodOrbTests.register(r);
        CapabilityTests.register(r);
        DataValidationTests.register(r);
        DungeonKeyLootTests.register(r);
        EssentiaVitaeFluidTests.register(r);
        FlaskRinseTests.register(r);
        FurnaceArrayTests.register(r);
        HarvestTests.register(r);
        HellfireForgeTests.register(r);
        ImperfectRitualTests.register(r);
        LexVitaeTests.register(r);
        MinorSystemTests.register(r);
        MultiblockTests.register(r);
        QuarryBackfillTests.register(r);
        QuarryThroughputTests.register(r);
        MeteorLandingTests.register(r);
        RecipeSyntaxTests.register(r);
        RitualReaderTests.register(r);
        RoutingNodeTests.register(r);
        SacrificeTests.register(r);
        SigilFakePlayerTests.register(r);
        BoundTreasuresTests.register(r);
        SpawnerCaptureTests.register(r);
        SpawnerSuppressionTests.register(r);
        SanguineWardTests.register(r);
        LightArrayTests.register(r);
        OmniRoutingNodeTests.register(r);
        SpiritAccumulatorTests.register(r);
        SpiritusRoutingTests.register(r);
        RoutingFilterRuleTests.register(r);
        SpiritusTests.register(r);
        TabulaVitaeTests.register(r);
        UnboundedFlightTests.register(r);
        AquaticArmorTests.register(r);
        ArrayRedstoneTests.register(r);
        VasMaleficumTests.register(r);
        EnchantedVitaeTests.register(r);
        EnchantedVitaeHaltTests.register(r);
        PlacerTests.register(r);
        AlternatorTests.register(r);
        TrainerLimitTests.register(r);
        SentientRitualTests.register(r);
        SanguineCrafterTests.register(r);
        SentientRepairTests.register(r);
        SentientClientSyncTests.register(r);
        GameplayRegressionTests.register(r);
        BloodLanternTests.register(r);
        BurdenGroundingTests.register(r);
        DeusToolTests.register(r);
        DeusRitualTests.register(r);
        HellforgingTests.register(r);
        DeusSanguinisTests.register(r);
        ChargeAliasTests.register(r);
        TrainingBraceletTests.register(r);
        EnderSeverArrayTests.register(r);
        SentientScytheEnchantTests.register(r);
        SentientSpearTests.register(r);
        SuppressionRitualTests.register(r);
        CelerityRitualTests.register(r);
        RitualAnchorTests.register(r);
        DungeonBlockRotationTests.register(r);
        DungeonRoomBoundsTests.register(r);
        PillarAccentTests.register(r);
    }
}
