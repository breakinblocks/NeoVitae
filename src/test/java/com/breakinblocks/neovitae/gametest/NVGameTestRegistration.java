package com.breakinblocks.neovitae.gametest;

import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class NVGameTestRegistration {

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(HarvestTests.class);
        event.register(RoutingNodeTests.class);
        event.register(AraVitaeTests.class);
        event.register(VitaeLinkTests.class);
        event.register(HellfireForgeTests.class);
        event.register(AthanorTests.class);
        event.register(TabulaVitaeTests.class);
        event.register(SpiritusTests.class);
        event.register(AlchemyArrayTests.class);
        event.register(ImperfectRitualTests.class);
        event.register(AnimaTests.class);
        event.register(MinorSystemTests.class);
        event.register(MultiblockTests.class);
        event.register(DataValidationTests.class);
        event.register(SacrificeTests.class);

        event.register(BloodOrbTests.class);
    }
}
