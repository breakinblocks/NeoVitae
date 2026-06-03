package com.breakinblocks.neovitae.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantingPowerBlock extends Block implements IEnchantmentAmplifier {
    private final float enchantPower;

    public EnchantingPowerBlock(Properties properties, float enchantPower) {
        super(properties);
        this.enchantPower = enchantPower;
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, BlockGetter level, BlockPos pos) {
        return enchantPower;
    }
}
