package com.breakinblocks.neovitae.client.model;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.item.OrbFluidHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import org.jspecify.annotations.Nullable;

/**
 * Exposes a Blood Orb's fill ratio (0.0 .. 1.0) to range_dispatch selectors so
 * the orb's per-tier fill-level variant models can be selected by current
 * {@code neovitae:orb_fluid} content relative to its data-map-derived capacity.
 */
public record OrbFillProperty() implements RangeSelectItemModelProperty {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(NeoVitae.MODID, "orb_fill");
    public static final MapCodec<OrbFillProperty> MAP_CODEC = MapCodec.unit(new OrbFillProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        int capacity = OrbFluidHandler.getOrbFluidCapacity(stack);
        if (capacity <= 0) return 0.0F;
        SimpleFluidContent content = stack.getOrDefault(NVDataComponents.ORB_FLUID.get(), SimpleFluidContent.EMPTY);
        int amount = content.isEmpty() ? 0 : content.getAmount();
        return (float) amount / (float) capacity;
    }

    @Override
    public MapCodec<OrbFillProperty> type() {
        return MAP_CODEC;
    }
}
