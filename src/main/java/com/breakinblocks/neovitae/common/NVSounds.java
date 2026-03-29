package com.breakinblocks.neovitae.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

public class NVSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, NeoVitae.MODID);

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(NeoVitae.rl(name)));
    }

    // Music
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_SWEAT_AND_TEARS_MUSIC = sound("bloodsweatandtears");

    // Blood Lamp
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_LAMP = sound("blood_lamp");

    // Ara Vitae (Blood Altar)
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_ALTAR_AMBIENT = sound("blood_altar_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_ALTAR_CRAFT_START = sound("blood_altar_craft_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_ALTAR_CRAFT_COMPLETE = sound("blood_altar_craft_complete");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_ALTAR_FAIL = sound("blood_altar_fail");

    // Rituals
    public static final DeferredHolder<SoundEvent, SoundEvent> RITUAL_ACTIVATE = sound("ritual_activate");
    public static final DeferredHolder<SoundEvent, SoundEvent> RITUAL_AMBIENT = sound("ritual_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> RITUAL_COMPLETE = sound("ritual_complete");

    // Alchemy Array
    public static final DeferredHolder<SoundEvent, SoundEvent> ALCHEMY_ARRAY_ACTIVATE = sound("alchemy_array_activate");
    public static final DeferredHolder<SoundEvent, SoundEvent> ALCHEMY_ARRAY_CRAFT = sound("alchemy_array_craft");

    // Hellfire Forge
    public static final DeferredHolder<SoundEvent, SoundEvent> HELLFIRE_FORGE_AMBIENT = sound("hellfire_forge_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> HELLFIRE_FORGE_CRAFT = sound("hellfire_forge_craft");
    public static final DeferredHolder<SoundEvent, SoundEvent> HELLFIRE_FORGE_COMPLETE = sound("hellfire_forge_complete");

    // Athanor
    public static final DeferredHolder<SoundEvent, SoundEvent> ATHANOR_BUBBLE = sound("athanor_bubble");
    public static final DeferredHolder<SoundEvent, SoundEvent> ATHANOR_COMPLETE = sound("athanor_complete");

    // Tabula Vitae
    public static final DeferredHolder<SoundEvent, SoundEvent> TABULA_VITAE_ACTIVATE = sound("tabula_vitae_activate");
    public static final DeferredHolder<SoundEvent, SoundEvent> TABULA_VITAE_CRAFT = sound("tabula_vitae_craft");
    public static final DeferredHolder<SoundEvent, SoundEvent> TABULA_VITAE_COMPLETE = sound("tabula_vitae_complete");

    // Teleposer
    public static final DeferredHolder<SoundEvent, SoundEvent> TELEPOSER_ACTIVATE = sound("teleposer_activate");
    public static final DeferredHolder<SoundEvent, SoundEvent> TELEPOSER_ARRIVAL = sound("teleposer_arrival");

    // Player / Combat
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_SIPHON = sound("blood_siphon");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_SHIELD_ABSORB = sound("blood_shield_absorb");

    // Sigils
    public static final DeferredHolder<SoundEvent, SoundEvent> SIGIL_ACTIVATE = sound("sigil_activate");
    public static final DeferredHolder<SoundEvent, SoundEvent> SIGIL_TOGGLE_ON = sound("sigil_toggle_on");
    public static final DeferredHolder<SoundEvent, SoundEvent> SIGIL_TOGGLE_OFF = sound("sigil_toggle_off");

    // Incense
    public static final DeferredHolder<SoundEvent, SoundEvent> INCENSE_IGNITE = sound("incense_ignite");
    public static final DeferredHolder<SoundEvent, SoundEvent> INCENSE_AMBIENT = sound("incense_ambient");

    // Blood Tank
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_TANK_FILL = sound("blood_tank_fill");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_TANK_DRAIN = sound("blood_tank_drain");

    // Inscription Tools
    public static final DeferredHolder<SoundEvent, SoundEvent> INSCRIPTION_TOOL_SCRIBE = sound("inscription_tool_scribe");

    public static void register(IEventBus modBus) {
        SOUND_EVENTS.register(modBus);
    }
}
