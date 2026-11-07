package com.breakinblocks.neovitae.common.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.effect.NVMobEffects;

@EventBusSubscriber(modid = NeoVitae.MODID)
public final class AnimaSeveranceHandler {

    private AnimaSeveranceHandler() {}

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect().is(NVMobEffects.ANIMA_SEVERANCE)) {
            event.setCanceled(true);
        }
    }
}
