package com.breakinblocks.neovitae.common.advancement;

import com.breakinblocks.neovitae.NeoVitae;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NVCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(Registries.TRIGGER_TYPE, NeoVitae.MODID);

    public static final DeferredHolder<CriterionTrigger<?>, RitualActivatedTrigger> RITUAL_ACTIVATED =
            TRIGGERS.register("ritual_activated", RitualActivatedTrigger::new);

    public static final DeferredHolder<CriterionTrigger<?>, ImperfectRitualActivatedTrigger> IMPERFECT_RITUAL_ACTIVATED =
            TRIGGERS.register("imperfect_ritual_activated", ImperfectRitualActivatedTrigger::new);

    public static void register(IEventBus modBus) {
        TRIGGERS.register(modBus);
    }
}
