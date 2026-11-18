package com.breakinblocks.neovitae.gametest;

import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class NVGameTestRegistration {

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(AnointmentTests.class);
        event.register(HarvestTests.class);
        event.register(RoutingNodeTests.class);
        event.register(AraVitaeTests.class);
        event.register(VitaeLinkTests.class);
        event.register(HellfireForgeTests.class);
        event.register(AthanorTests.class);
        event.register(TabulaVitaeTests.class);
        event.register(UnboundedFlightTests.class);
        event.register(SpiritusTests.class);
        event.register(AlchemyArrayTests.class);
        event.register(ImperfectRitualTests.class);
        event.register(AnimaTests.class);
        event.register(MinorSystemTests.class);
        event.register(MultiblockTests.class);
        event.register(DataValidationTests.class);
        event.register(DungeonKeyLootTests.class);
        event.register(RecipeSyntaxTests.class);
        event.register(SacrificeTests.class);
        event.register(SpawnerSuppressionTests.class);
        event.register(SpawnerCaptureTests.class);
        event.register(EssentiaVitaeFluidTests.class);
        event.register(BoundTreasuresTests.class);
        event.register(QuarryBackfillTests.class);
        event.register(QuarryThroughputTests.class);
        event.register(MeteorLandingTests.class);
        event.register(SanguineCrafterTests.class);
        event.register(SuppressionRitualTests.class);
        event.register(CelerityRitualTests.class);
        event.register(DungeonBlockRotationTests.class);

        event.register(BloodOrbTests.class);
        event.register(SpiritAccumulatorTests.class);
        event.register(VasMaleficumTests.class);
        event.register(SpiritusRoutingTests.class);
        event.register(RitualReaderTests.class);
        event.register(SanguineWardTests.class);
        event.register(EnchantedVitaeTests.class);
        event.register(EnchantedVitaeHaltTests.class);
        event.register(PlacerTests.class);
        event.register(AlternatorTests.class);
        event.register(TrainerLimitTests.class);
        event.register(FlaskRinseTests.class);
        event.register(BurdenGroundingTests.class);
        event.register(DeusToolTests.class);
        event.register(DeusRitualTests.class);
        event.register(RitualAnchorTests.class);
        event.register(EnderSeverArrayTests.class);
        event.register(SentientScytheEnchantTests.class);
        event.register(NewMoonTests.class);
        event.register(OvergrowthTests.class);
    }
}
