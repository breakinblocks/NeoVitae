package com.breakinblocks.neovitae.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

/**
 * Registers all custom sound events for NeoVitae.
 */
public class NVSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, NeoVitae.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BLEEDING_EDGE_MUSIC = SOUND_EVENTS.register("bleedingedge",
            () -> SoundEvent.createVariableRangeEvent(NeoVitae.rl("bleedingedge")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_LAMP = SOUND_EVENTS.register("blood_lamp",
            () -> SoundEvent.createVariableRangeEvent(NeoVitae.rl("blood_lamp")));

    public static void register(IEventBus modBus) {
        SOUND_EVENTS.register(modBus);
    }
}
