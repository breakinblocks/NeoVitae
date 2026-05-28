package com.breakinblocks.neovitae.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.common.advancement.NVCriteriaTriggers;
import com.breakinblocks.neovitae.common.block.BlockSpiritusCrystal;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.blockentity.SpiritusCrystalBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.SpiritusType;

import java.util.List;

public class CrystalCatalystItem extends Item {

    private final SpiritusType type;
    private final double injectedSpiritus;
    private final double speedModifier;
    private final double conversionRate;
    private final double maxInjectedSpiritus;

    public CrystalCatalystItem(SpiritusType type, double injectedSpiritus, double speedModifier, double conversionRate, double maxInjectedSpiritus) {
        super(new Properties());
        this.type = type;
        this.injectedSpiritus = injectedSpiritus;
        this.speedModifier = speedModifier;
        this.conversionRate = conversionRate;
        this.maxInjectedSpiritus = maxInjectedSpiritus;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.neovitae.crystal_catalyst.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.neovitae.crystal_catalyst.aspect", type.toCapitalized()).withStyle(ChatFormatting.DARK_PURPLE));
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();

        BlockEntity tile = level.getBlockEntity(pos);
        if (!(tile instanceof SpiritusCrystalBlockEntity crystalTile)) {
            return InteractionResult.PASS;
        }

        SpiritusType clusterType = crystalTile.getSpiritusType();
        BlockState state = level.getBlockState(pos);

        if (type != SpiritusType.RAW && clusterType == SpiritusType.RAW) {
            return tryTransmute(level, pos, state, stack, player);
        }

        if (!level.isClientSide && applyCatalyst(crystalTile)) {
            ServerLevel server = (ServerLevel) level;
            ItemStack crystalStack = BlockSpiritusCrystal.getItemStackDropped(type, 1);
            ItemParticleOption particleData = new ItemParticleOption(ParticleTypes.ITEM, crystalStack);
            for (int i = 0; i < 8; ++i) {
                server.sendParticles(particleData, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 1, 0.2, 0.2, 0.2, 0.03);
            }
            stack.shrink(1);
            level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1, 1);
        }
        return InteractionResult.SUCCESS;
    }

    private InteractionResult tryTransmute(Level level, BlockPos pos, BlockState state, ItemStack stack, Player player) {
        if (!state.hasProperty(BlockSpiritusCrystal.AGE) || state.getValue(BlockSpiritusCrystal.AGE) != 6) {
            if (level.isClientSide && player != null) {
                player.displayClientMessage(Component.translatable("chat.neovitae.crystal_catalyst.notMature").withStyle(ChatFormatting.YELLOW), true);
            }
            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player == null || !consumeAnimusMote(player.getInventory())) {
            if (player != null) {
                player.displayClientMessage(Component.translatable("chat.neovitae.crystal_catalyst.needsAnimus").withStyle(ChatFormatting.RED), true);
            }
            return InteractionResult.SUCCESS;
        }

        Block target = blockForAspect(type);
        if (target == null) return InteractionResult.SUCCESS;

        Direction attached = state.hasProperty(BlockSpiritusCrystal.ATTACHED)
                ? state.getValue(BlockSpiritusCrystal.ATTACHED)
                : Direction.UP;
        BlockState transmuted = target.defaultBlockState()
                .setValue(BlockSpiritusCrystal.ATTACHED, attached)
                .setValue(BlockSpiritusCrystal.AGE, 0);
        level.setBlock(pos, transmuted, Block.UPDATE_ALL);

        ServerLevel server = (ServerLevel) level;
        ItemStack crystalStack = BlockSpiritusCrystal.getItemStackDropped(type, 1);
        ItemParticleOption particleData = new ItemParticleOption(ParticleTypes.ITEM, crystalStack);
        for (int i = 0; i < 16; ++i) {
            server.sendParticles(particleData, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 1, 0.3, 0.3, 0.3, 0.05);
        }
        stack.shrink(1);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1, 1.2f);

        if (player instanceof ServerPlayer sp) {
            NVCriteriaTriggers.CATALYST_TRANSMUTE.get().trigger(sp, type);
        }
        return InteractionResult.SUCCESS;
    }

    private boolean consumeAnimusMote(Inventory inv) {
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack inSlot = inv.getItem(i);
            if (inSlot.is(NVItems.ANIMUS_MOTE.get())) {
                inSlot.shrink(1);
                return true;
            }
        }
        return false;
    }

    private static Block blockForAspect(SpiritusType type) {
        return switch (type) {
            case RUINA -> NVBlocks.SPIRITUS_RUINA_CRYSTAL.block().get();
            case NIHILUM -> NVBlocks.SPIRITUS_NIHILUM_CRYSTAL.block().get();
            case VINDICTA -> NVBlocks.SPIRITUS_VINDICTA_CRYSTAL.block().get();
            case INVICTUS -> NVBlocks.SPIRITUS_INVICTUS_CRYSTAL.block().get();
            case RAW -> null;
        };
    }

    private boolean applyCatalyst(SpiritusCrystalBlockEntity crystalTile) {
        if (type.equals(crystalTile.getSpiritusType()) && (crystalTile.injectedSpiritus + injectedSpiritus) <= maxInjectedSpiritus) {
            crystalTile.applyCatalyst(injectedSpiritus, speedModifier, conversionRate);
            return true;
        }
        return false;
    }
}
