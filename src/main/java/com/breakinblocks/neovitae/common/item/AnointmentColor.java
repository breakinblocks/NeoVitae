package com.breakinblocks.neovitae.common.item;

import com.breakinblocks.neovitae.NeoVitae;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Tints alchemic_liquid layer based on the anointment provider's configured color.
 * Registered via {@code RegisterColorHandlersEvent.ItemTintSources} and referenced from
 * {@code assets/neovitae/items/<id>.json} under {@code tints}.
 */
public record AnointmentColor() implements ItemTintSource {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "anointment");
    public static final MapCodec<AnointmentColor> MAP_CODEC = MapCodec.unit(new AnointmentColor());

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        if (stack.getItem() instanceof ItemAnointmentProvider provider) {
            return 0xFF000000 | provider.getColor();
        }
        return 0xFFFFFFFF;
    }

    @Override
    public MapCodec<AnointmentColor> type() {
        return MAP_CODEC;
    }
}
