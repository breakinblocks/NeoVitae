package com.breakinblocks.neovitae.common.attribute;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.breakinblocks.neovitae.NeoVitae;

public class NVAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, NeoVitae.MODID);

    public static final DeferredHolder<Attribute, PercentageAttribute> SELF_SACRIFICE_MULTIPLIER = ATTRIBUTES.register("player.self_sacrifice_multiplier",
            () -> new PercentageAttribute("attribute.neovitae.player.self_sacrifice", 1, 0, 100));

    public static final DeferredHolder<Attribute, Attribute> BONUS_SACRIFICE = ATTRIBUTES.register("bonus_sacrifice",
            () -> new RangedAttribute("attribute.neovitae.bonus_sacrifice", 0.0D, 0.0D, 1000.0D).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> BONUS_SELF_SACRIFICE = ATTRIBUTES.register("bonus_self_sacrifice",
            () -> new RangedAttribute("attribute.neovitae.bonus_self_sacrifice", 0.0D, 0.0D, 1000.0D).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> BONUS_DEMON_WILL = ATTRIBUTES.register("bonus_demon_will",
            () -> new RangedAttribute("attribute.neovitae.bonus_demon_will", 0.0D, 0.0D, 1000.0D).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> SIGIL_COST_REDUCTION = ATTRIBUTES.register("sigil_cost_reduction",
            () -> new RangedAttribute("attribute.neovitae.sigil_cost_reduction", 0.0D, 0.0D, 100.0D).setSyncable(true));

    private static void addToPlayer(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, SELF_SACRIFICE_MULTIPLIER, 1);
        event.add(EntityType.PLAYER, BONUS_SACRIFICE, 0);
        event.add(EntityType.PLAYER, BONUS_SELF_SACRIFICE, 0);
        event.add(EntityType.PLAYER, BONUS_DEMON_WILL, 0);
        event.add(EntityType.PLAYER, SIGIL_COST_REDUCTION, 0);
    }

    public static void register(IEventBus modBus) {
        ATTRIBUTES.register(modBus);
        modBus.addListener(NVAttributes::addToPlayer);
    }
}
