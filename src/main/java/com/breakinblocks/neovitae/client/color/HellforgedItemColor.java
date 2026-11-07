package com.breakinblocks.neovitae.client.color;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.helper.HellforgedTooltipHelper;
import com.breakinblocks.neovitae.common.item.soul.HellforgedHelper;

public record HellforgedItemColor() implements ItemTintSource {

    public static final Identifier ID = NeoVitae.rl("hellforged");
    public static final MapCodec<HellforgedItemColor> MAP_CODEC = MapCodec.unit(new HellforgedItemColor());

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        return HellforgedHelper.isHellforged(stack) ? 0xFF000000 | HellforgedTooltipHelper.HELLFORGED_COLOR : 0xFFFFFFFF;
    }

    @Override
    public MapCodec<HellforgedItemColor> type() {
        return MAP_CODEC;
    }
}
