package com.breakinblocks.neovitae.common.blockentity;

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
import com.breakinblocks.neovitae.common.blockentity.base.TickingTile;
import com.breakinblocks.neovitae.common.datacomponent.AnointmentHolder;
import com.breakinblocks.neovitae.common.datacomponent.BMDataComponents;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;

/**
 * Base class for explosive charge block entities.
 * Handles owner tracking, anointments, countdown effects, and block breaking logic.
 */
public class ExplosiveChargeTile extends TickingTile {
    protected UUID ownerUUID;
    protected AnointmentHolder anointmentHolder = AnointmentHolder.empty();
    protected double internalCounter = 0;

    // Countdown timing constants
    protected static final int IGNITE_TICK = 20;
    protected static final int PRIME_TICK = 30;
    protected static final int EXPLODE_TICK = 100;

    public ExplosiveChargeTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
            stack.set(BMDataComponents.ANOINTMENT_HOLDER, anointmentHolder);
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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("ownerUUID")) {
            ownerUUID = tag.getUUID("ownerUUID");
        }
        if (tag.contains("anointment_holder")) {
            anointmentHolder = AnointmentHolder.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("anointment_holder"))
                    .result()
                    .orElse(AnointmentHolder.empty());
        }
        internalCounter = tag.getDouble("internalCounter");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (ownerUUID != null) {
            tag.putUUID("ownerUUID", ownerUUID);
        }
        if (anointmentHolder != null && !anointmentHolder.isEmpty()) {
            AnointmentHolder.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), anointmentHolder)
                    .result()
                    .ifPresent(nbt -> tag.put("anointment_holder", nbt));
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
            stack.set(BMDataComponents.ANOINTMENT_HOLDER, anointmentHolder);
        }
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
    }

    @Override
    public void onUpdate() {
        // Override in subclasses
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
                    1.0F, level.random.nextFloat() * 0.4F + 0.8F);
            ((ServerLevel) level).sendParticles(ParticleTypes.FLAME,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    5, 0.02, 0.03, 0.02, 0);
        }

        if (internalCounter == PRIME_TICK) {
            level.playSound((Player) null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if (internalCounter >= PRIME_TICK && level.random.nextDouble() < 0.3) {
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

        // Play explosion sound
        level.playSound((Player) null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS,
                4.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

        // Spawn explosion particles
        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                worldPosition.getX() + 0.5 + explosiveDirection.getStepX(),
                worldPosition.getY() + 0.5 + explosiveDirection.getStepY(),
                worldPosition.getZ() + 0.5 + explosiveDirection.getStepZ(),
                10, 1.0D, 1.0D, 1.0D, 0);

        // Collect and merge drops
        ObjectArrayList<Pair<ItemStack, BlockPos>> dropList = new ObjectArrayList<>();

        for (BlockPos blockPos : blocksToBreak) {
            BlockState blockstate = level.getBlockState(blockPos);

            if (blockstate.isAir()) {
                continue;
            }

            // Check protection before breaking
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

        // Drop all collected items
        for (Pair<ItemStack, BlockPos> pair : dropList) {
            Block.popResource(level, pair.getSecond(), pair.getFirst());
        }

        // Remove this explosive block
        level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
    }

    /**
     * Resets the countdown counter. Call this when starting a new countdown sequence.
     */
    protected void resetCounter() {
        internalCounter = 0;
    }

    /**
     * Gets the current countdown value.
     */
    protected double getCounter() {
        return internalCounter;
    }
}
