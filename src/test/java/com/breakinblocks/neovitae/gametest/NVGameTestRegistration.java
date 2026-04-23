package com.breakinblocks.neovitae.gametest;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.gametest.base.NVTestRegistrar;

@EventBusSubscriber(modid = NeoVitae.MODID)
public class NVGameTestRegistration {

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        NVTestRegistrar r = new NVTestRegistrar(event);

        AlchemyArrayTests.register(r);
        AnimaTests.register(r);
        AraVitaeTests.register(r);
        AthanorTests.register(r);
        BloodOrbTests.register(r);
        CapabilityTests.register(r);
        DataValidationTests.register(r);
        HellfireForgeTests.register(r);
        ImperfectRitualTests.register(r);
        MinorSystemTests.register(r);
        MultiblockTests.register(r);
        RoutingNodeTests.register(r);
        SacrificeTests.register(r);
        SpiritusTests.register(r);
        TabulaVitaeTests.register(r);
    }
}
