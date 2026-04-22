package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import com.breakinblocks.neovitae.common.blockentity.base.TickingBlockEntity;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;

/**
 * Base class for explosive charge block entities.
 * Handles owner tracking, anointments, countdown effects, and block breaking logic.
 */
public class ExplosiveChargeBlockEntity extends TickingBlockEntity {
    protected UUID ownerUUID;
    protected AnointmentHolder anointmentHolder = AnointmentHolder.empty();
    protected double internalCounter = 0;

    // Countdown timing constants
    protected static final int IGNITE_TICK = 20;
    protected static final int PRIME_TICK = 30;
    protected static final int EXPLODE_TICK = 100;

    public ExplosiveChargeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected static void handleExplosionDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> dropPositionArray, ItemStack stack, BlockPos pos) {
        int i = dropPositionArray.size();

        for (int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = dropPositionArray.get(j);
            ItemStack itemstack = pair.getFirst();
            if (ItemEntity.areMergable(itemstack, stack)) {
                ItemStack itemstack1 = ItemEntity.merge(itemstack, stack, 16);
                dropPositionArray.set(j, Pair.of(itemstack1, pair.getSecond()));
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        dropPositionArray.add(Pair.of(stack, pos));
    }

    public ItemStack getHarvestingTool() {
        ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
        if (anointmentHolder != null && !anointmentHolder.isEmpty()) {
            stack.set(NVDataComponents.ANOINTMENT_HOLDER, anointmentHolder);
        }
        return stack;
    }

    public void setAnointmentHolder(AnointmentHolder holder) {
        this.anointmentHolder = holder;
    }

    public AnointmentHolder getAnointmentHolder() {
        return anointmentHolder;
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        ownerUUID = tag.read("ownerUUID", UUIDUtil.CODEC).orElse(null);
        anointmentHolder = tag.read("anointment_holder", AnointmentHolder.CODEC).orElse(AnointmentHolder.empty());
        internalCounter = tag.getDoubleOr("internalCounter", 0d);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        if (ownerUUID != null) {
            tag.store("ownerUUID", UUIDUtil.CODEC, ownerUUID);
        }
        if (anointmentHolder != null && !anointmentHolder.isEmpty()) {
            tag.store("anointment_holder", AnointmentHolder.CODEC, anointmentHolder);
        }
        tag.putDouble("internalCounter", internalCounter);
    }

    public void setOwner(UUID uuid) {
        this.ownerUUID = uuid;
    }

    public UUID getOwner() {
        return ownerUUID;
    }

    public void dropSelf() {
        ItemStack stack = new ItemStack(getBlockState().getBlock());
        if (anointmentHolder != null && !anointmentHolder.isEmpty()) {
            stack.set(NVDataComponents.ANOINTMENT_HOLDER, anointmentHolder);
        }
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
    }

    @Override
    public void onUpdate() {
    }

    /**
     * Increments the internal counter and plays countdown sound/particle effects.
     * Call this each tick after block scanning is complete.
     *
     * @return true if the explosion should occur (counter reached EXPLODE_TICK)
     */
    protected boolean tickCountdown() {
        internalCounter++;

        if (internalCounter == IGNITE_TICK) {
            level.playSound((Player) null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS,
                    1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            ((ServerLevel) level).sendParticles(ParticleTypes.FLAME,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    5, 0.02, 0.03, 0.02, 0);
        }

        if (internalCounter == PRIME_TICK) {
            level.playSound((Player) null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if (internalCounter >= PRIME_TICK && level.getRandom().nextDouble() < 0.3) {
            ((ServerLevel) level).sendParticles(ParticleTypes.SMOKE,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    1, 0.0D, 0.0D, 0.0D, 0);
        }

        return internalCounter >= EXPLODE_TICK;
    }

    /**
     * Performs the explosion effect and breaks all blocks in the given list.
     * Handles loot drops, protection checks, and removes this block entity.
     *
     * @param explosiveDirection The direction the explosive is facing
     * @param blocksToBreak List of block positions to break
     */
    protected void explodeAndBreakBlocks(Direction explosiveDirection, List<BlockPos> blocksToBreak) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ItemStack toolStack = getHarvestingTool();

        level.playSound((Player) null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                4.0F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);

        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                worldPosition.getX() + 0.5 + explosiveDirection.getStepX(),
                worldPosition.getY() + 0.5 + explosiveDirection.getStepY(),
                worldPosition.getZ() + 0.5 + explosiveDirection.getStepZ(),
                10, 1.0D, 1.0D, 1.0D, 0);

        ObjectArrayList<Pair<ItemStack, BlockPos>> dropList = new ObjectArrayList<>();

        for (BlockPos blockPos : blocksToBreak) {
            BlockState blockstate = level.getBlockState(blockPos);

            if (blockstate.isAir()) {
                continue;
            }

            if (!BlockProtectionHelper.canBreakBlockStrict(level, blockPos, ownerUUID)) {
                continue;
            }

            BlockPos immutablePos = blockPos.immutable();
            BlockEntity blockEntity = blockstate.getBlock() instanceof EntityBlock
                    ? level.getBlockEntity(blockPos)
                    : null;

            LootParams.Builder lootBuilder = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos))
                    .withParameter(LootContextParams.TOOL, toolStack)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);

            blockstate.getDrops(lootBuilder).forEach(stack ->
                    handleExplosionDrops(dropList, stack, immutablePos));

            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
        }

        for (Pair<ItemStack, BlockPos> pair : dropList) {
            Block.popResource(level, pair.getSecond(), pair.getFirst());
        }

        level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
    }

    protected void resetCounter() {
        internalCounter = 0;
    }

    protected double getCounter() {
        return internalCounter;
    }
}
