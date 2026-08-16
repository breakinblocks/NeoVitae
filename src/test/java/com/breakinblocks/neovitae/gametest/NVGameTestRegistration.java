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
        BloodOrbTests.register(r);
        CapabilityTests.register(r);
        DataValidationTests.register(r);
        EssentiaLoggingTests.register(r);
        HarvestTests.register(r);
        HellfireForgeTests.register(r);
        ImperfectRitualTests.register(r);
        LexVitaeTests.register(r);
        MinorSystemTests.register(r);
        MultiblockTests.register(r);
        QuarryBackfillTests.register(r);
        RecipeSyntaxTests.register(r);
        RoutingNodeTests.register(r);
        SacrificeTests.register(r);
        SpawnerSuppressionTests.register(r);
        SanguineWardTests.register(r);
        SpiritAccumulatorTests.register(r);
        SpiritusRoutingTests.register(r);
        SpiritusTests.register(r);
        TabulaVitaeTests.register(r);
        VasMaleficumTests.register(r);
    }
}
