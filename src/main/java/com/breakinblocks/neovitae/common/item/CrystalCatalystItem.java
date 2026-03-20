package com.breakinblocks.neovitae.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.block.BlockDemonCrystal;
import com.breakinblocks.neovitae.common.blockentity.DemonCrystalBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.EnumWillType;

public class CrystalCatalystItem extends Item {

    private final EnumWillType type;
    private final double injectedWill;
    private final double speedModifier;
    private final double conversionRate;
    private final double maxInjectedWill;

    public CrystalCatalystItem(EnumWillType type, double injectedWill, double speedModifier, double conversionRate, double maxInjectedWill) {
        super(new Properties());
        this.type = type;
        this.injectedWill = injectedWill;
        this.speedModifier = speedModifier;
        this.conversionRate = conversionRate;
        this.maxInjectedWill = maxInjectedWill;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof DemonCrystalBlockEntity crystalTile) {
            if (!level.isClientSide) {
                if (applyCatalyst(crystalTile)) {
                    ServerLevel server = (ServerLevel) level;
                    ItemStack crystalStack = BlockDemonCrystal.getItemStackDropped(type, 1);

                    ItemParticleOption particleData = new ItemParticleOption(ParticleTypes.ITEM, crystalStack);
                    for (int i = 0; i < 8; ++i) {
                        server.sendParticles(particleData, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 1, 0.2, 0.2, 0.2, 0.03);
                    }
                    stack.shrink(1);
                    level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1, 1);
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private boolean applyCatalyst(DemonCrystalBlockEntity crystalTile) {
        if (type.equals(crystalTile.getWillType()) && (crystalTile.injectedWill + injectedWill) <= maxInjectedWill) {
            crystalTile.applyCatalyst(injectedWill, speedModifier, conversionRate);
            return true;
        }
        return false;
    }
}
