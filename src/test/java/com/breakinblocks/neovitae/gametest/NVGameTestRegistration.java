package com.breakinblocks.neovitae.gametest;

import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class NVGameTestRegistration {

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(RoutingNodeTests.class);
    }
}
