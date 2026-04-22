package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.breakinblocks.neovitae.common.block.BlockSpiritusCrystal;
import com.breakinblocks.neovitae.common.blockentity.SpiritusCrystalBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;

public class CrystalCatalystItem extends Item {

    private final SpiritusType type;
    private final double injectedWill;
    private final double speedModifier;
    private final double conversionRate;
    private final double maxInjectedWill;

    public CrystalCatalystItem(Item.Properties props, SpiritusType type, double injectedWill, double speedModifier, double conversionRate, double maxInjectedWill) {
        super(props);
        this.type = type;
        this.injectedWill = injectedWill;
        this.speedModifier = speedModifier;
        this.conversionRate = conversionRate;
        this.maxInjectedWill = maxInjectedWill;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.neovitae.crystal_catalyst.desc").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("tooltip.neovitae.crystal_catalyst.aspect", type.toCapitalized()).withStyle(ChatFormatting.DARK_PURPLE));}

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof SpiritusCrystalBlockEntity crystalTile) {
            if (!level.isClientSide()) {
                if (applyCatalyst(crystalTile)) {
                    ServerLevel server = (ServerLevel) level;
                    ItemStack crystalStack = BlockSpiritusCrystal.getItemStackDropped(type, 1);

                    ItemParticleOption particleData = new ItemParticleOption(ParticleTypes.ITEM, crystalStack.getItem());
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

    private boolean applyCatalyst(SpiritusCrystalBlockEntity crystalTile) {
        if (type.equals(crystalTile.getWillType()) && (crystalTile.injectedWill + injectedWill) <= maxInjectedWill) {
            crystalTile.applyCatalyst(injectedWill, speedModifier, conversionRate);
            return true;
        }
        return false;
    }
}
