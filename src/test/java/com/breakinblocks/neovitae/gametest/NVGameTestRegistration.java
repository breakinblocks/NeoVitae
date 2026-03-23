package com.breakinblocks.neovitae.gametest;

import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class NVGameTestRegistration {

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(RoutingNodeTests.class);
        event.register(BloodAltarTests.class);
        event.register(HellfireForgeTests.class);
        event.register(ARCTests.class);
        event.register(AlchemyTableTests.class);
        event.register(DemonWillTests.class);
        event.register(AlchemyArrayTests.class);
        event.register(ImperfectRitualTests.class);
        event.register(SoulNetworkTests.class);
        event.register(MinorSystemTests.class);
        event.register(MultiblockTests.class);
        event.register(DataValidationTests.class);
    }
}
