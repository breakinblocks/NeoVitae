// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.spiritus.SpiritusState;
import com.breakinblocks.neovitae.common.block.NVBlocks;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class RitualMagnetism extends Ritual {

    public static final String PLACEMENT_RANGE = "placementRange";
    public static final String CHEST_RANGE = "chestRange";
    private static final int MAX_CHECKS_PER_REFRESH = 1000;
    private static final int MAX_POSITIONS_PER_REFRESH = 16384;
    private static final int MAX_ORES_PER_REFRESH = 30;
    private static final int MAX_FILLER_PER_REFRESH = 30;
    private static final int FILLER_COST = 10;
    private static final double STEADFAST_DRAIN = 0.01;
    private static final double CORROSIVE_DRAIN = 0.1;
    private static final float CORROSIVE_DRAIN_CHANCE = 0.1F;
    private static final int MAX_QUARRY_RADIUS = 255;

    private BlockPos lastPos;
    private int lastRadius = -1;

    public RitualMagnetism() {
        this("magnetism", 0, 5000);
    }

    protected RitualMagnetism(String name, int crystalLevel, int activationCost) {
        super(name, crystalLevel, activationCost, "ritual." + NeoVitae.MODID + "." + name);
        addBlockRange(PLACEMENT_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-1, 1, -1), 3, 3, 3));
        setMaximumVolumeAndDistanceOfRange(PLACEMENT_RANGE, 50, 4, 4);
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        Level world = ctx.level();
        BlockPos masterPos = ctx.masterPos();
        UUID owner = ctx.master().getOwner();

        SpiritusState will = RitualHelper.querySpiritus(world, masterPos, STEADFAST_DRAIN);
        boolean voidFiller = voidsFiller(ctx.master(), will);
        boolean backfill = !voidFiller && backfillsOres(ctx.master(), will);

        BlockPos chestPos = RitualHelper.firstPositionInRange(ctx.master(), this, CHEST_RANGE, masterPos).orElse(masterPos.above());
        IItemHandler container = world.getCapability(Capabilities.ItemHandler.BLOCK, chestPos, null);
        BlockPos foundationPos = masterPos.below();
        int radius = getRadius(world.getBlockState(foundationPos).getBlock());
        if (radius != lastRadius) {
            if (lastRadius >= 0) lastPos = null;
            lastRadius = radius;
        }
        int minRelY = world.getMinBuildHeight() - masterPos.getY();
        int oreCost = getRefreshCost();

        int i = -radius, j = -1, k = -radius;
        if (lastPos != null) {
            i = clamp(lastPos.getX(), -radius, radius);
            j = lastPos.getY();
            k = clamp(lastPos.getZ(), -radius, radius);
        }

        int checks = 0;
        int visited = 0;
        int oresMoved = 0;
        int fillerVoided = 0;
        int evLeft = ctx.currentEV();
        boolean finished = true;

        scan:
        while (j >= minRelY) {
            while (i <= radius) {
                while (k <= radius) {
                    if (checks >= MAX_CHECKS_PER_REFRESH * getScanMultiplier()
                            || visited >= MAX_POSITIONS_PER_REFRESH * getScanMultiplier()
                            || oresMoved >= getMaxOresPerRefresh()
                            || fillerVoided >= MAX_FILLER_PER_REFRESH * getScanMultiplier()
                            || evLeft < Math.max(oreCost, FILLER_COST)) {
                        finished = false;
                        break scan;
                    }
                    visited++;

                    BlockPos srcPos = masterPos.offset(i, j, k);
                    BlockState state = world.getBlockState(srcPos);
                    if (state.isAir() || state.is(NVTags.Blocks.QUARRY_BLACKLIST)) {
                        k++;
                        continue;
                    }
                    checks++;

                    if (state.is(NVTags.Blocks.QUARRY_BLOCKS)) {
                        if (BlockProtectionHelper.canBreakBlock(world, srcPos, owner)
                                && moveOre(ctx, world, srcPos, state, container, masterPos, backfill, will)) {
                            oresMoved++;
                            evLeft -= oreCost;
                        }
                    } else if (voidFiller
                            && state.is(NVTags.Blocks.QUARRY_FILLER)
                            && !srcPos.equals(foundationPos)
                            && BlockProtectionHelper.canBreakBlock(world, srcPos, owner)) {
                        world.setBlock(srcPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                        ctx.syphon(FILLER_COST);
                        fillerVoided++;
                        evLeft -= FILLER_COST;
                    }
                    k++;
                }
                k = -radius;
                i++;
            }
            i = -radius;
            j--;
        }

        lastPos = finished ? new BlockPos(-radius, -1, -radius) : new BlockPos(i, j, k);

        if (consumesQuarrySpiritus() && fillerVoided > 0 && world.random.nextFloat() < CORROSIVE_DRAIN_CHANCE) {
            RitualHelper.drainSpiritus(will, world, masterPos, 0, CORROSIVE_DRAIN, 0, 0, 0);
        }
    }

    protected boolean voidsFiller(IMasterRitualStone master, SpiritusState will) {
        return will.getRuina() >= CORROSIVE_DRAIN;
    }

    protected boolean backfillsOres(IMasterRitualStone master, SpiritusState will) {
        return will.hasInvictus() && will.getInvictus() >= STEADFAST_DRAIN;
    }

    protected boolean consumesQuarrySpiritus() {
        return true;
    }

    protected int getMaxOresPerRefresh() {
        return MAX_ORES_PER_REFRESH;
    }

    protected int getScanMultiplier() {
        return 1;
    }

    protected boolean usesFortune(IMasterRitualStone master) {
        return false;
    }

    private boolean moveOre(RitualContext ctx, Level world, BlockPos srcPos, BlockState state,
                            IItemHandler container, BlockPos masterPos,
                            boolean backfill, SpiritusState will) {
        if (container != null && usesFortune(ctx.master()) && world instanceof ServerLevel serverLevel) {
            return mineWithFortune(ctx, serverLevel, srcPos, state, container, masterPos, backfill, will);
        }
        if (container != null) {
            ItemStack oreStack = new ItemStack(state.getBlock().asItem());
            if (!oreStack.isEmpty() && oreStack.getItem() != Items.AIR) {
                ItemStack remainder = insertAll(container, oreStack);
                if (remainder.isEmpty()) {
                    clearOre(world, srcPos, backfill, will, masterPos);
                    ctx.syphon(getRefreshCost());
                    return true;
                }
            }
        }

        BlockPos destination = findFreePlacementSlot(ctx, masterPos);
        if (destination == null) return false;
        if (!world.isEmptyBlock(destination)) return false;
        if (!world.setBlock(destination, state, Block.UPDATE_ALL)) return false;
        clearOre(world, srcPos, backfill, will, masterPos);
        ctx.syphon(getRefreshCost());
        return true;
    }

    private boolean mineWithFortune(RitualContext ctx, ServerLevel level, BlockPos srcPos, BlockState state,
                                    IItemHandler container, BlockPos masterPos,
                                    boolean backfill, SpiritusState will) {
        ItemStack tool = RitualHelper.createMiningTool(level, true, false);
        List<ItemStack> drops = Block.getDrops(state, level, srcPos, level.getBlockEntity(srcPos), null, tool);
        if (drops.isEmpty()) return false;
        for (ItemStack drop : drops) {
            if (!ItemHandlerHelper.insertItemStacked(container, drop.copy(), true).isEmpty()) {
                return false;
            }
        }
        for (ItemStack drop : drops) {
            ItemHandlerHelper.insertItemStacked(container, drop, false);
        }
        clearOre(level, srcPos, backfill, will, masterPos);
        ctx.syphon(getRefreshCost());
        return true;
    }

    /**
     * Leaves the mined slot as air, or packs it with the surrounding stone when the ritual is
     * fed Spiritus Invictus, so a long-running quarry does not hollow out the ground beneath it.
     */
    private void clearOre(Level world, BlockPos srcPos, boolean backfill, SpiritusState will, BlockPos masterPos) {
        if (backfill && (!consumesQuarrySpiritus() || will.getInvictus() >= STEADFAST_DRAIN)) {
            world.setBlock(srcPos, fillerFor(world, srcPos), Block.UPDATE_ALL);
            if (consumesQuarrySpiritus()) {
                RitualHelper.drainSpiritus(will, world, masterPos, 0, 0, 0, 0, STEADFAST_DRAIN);
            }
            return;
        }
        world.setBlock(srcPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    }

    /**
     * Matches the surrounding rock so the fill is invisible, falling back to plain stone.
     * Only natural stone is copied, so nothing the player built can be duplicated.
     */
    public static BlockState fillerFor(Level world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockState neighbour = world.getBlockState(pos.relative(direction));
            if (neighbour.is(BlockTags.BASE_STONE_OVERWORLD)
                    || neighbour.is(BlockTags.BASE_STONE_NETHER)
                    || neighbour.is(Blocks.END_STONE)) {
                return neighbour;
            }
        }
        return Blocks.STONE.defaultBlockState();
    }

    private static ItemStack insertAll(IItemHandler handler, ItemStack stack) {
        ItemStack remainder = stack;
        for (int slot = 0; slot < handler.getSlots() && !remainder.isEmpty(); slot++) {
            remainder = handler.insertItem(slot, remainder, false);
        }
        return remainder;
    }

    private BlockPos findFreePlacementSlot(RitualContext ctx, BlockPos masterPos) {
        for (BlockPos offset : RitualHelper.getRangePositions(ctx.master(), this, PLACEMENT_RANGE, masterPos)) {
            if (ctx.level().isEmptyBlock(offset)) {
                return offset.immutable();
            }
        }
        return null;
    }

    public static void notifyDeposit(ServerLevel level, BlockPos center, int depositRadius) {
        int reach = MAX_QUARRY_RADIUS + depositRadius;
        int minChunkX = SectionPos.blockToSectionCoord(center.getX() - reach);
        int maxChunkX = SectionPos.blockToSectionCoord(center.getX() + reach);
        int minChunkZ = SectionPos.blockToSectionCoord(center.getZ() - reach);
        int maxChunkZ = SectionPos.blockToSectionCoord(center.getZ() + reach);
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) continue;
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (blockEntity instanceof IMasterRitualStone master && master.isActive()
                            && master.getCurrentRitual() instanceof RitualMagnetism quarry) {
                        quarry.rewindForDeposit(level, blockEntity.getBlockPos(), center, depositRadius);
                    }
                }
            }
        }
    }

    private void rewindForDeposit(Level world, BlockPos masterPos, BlockPos center, int depositRadius) {
        int radius = getRadius(world.getBlockState(masterPos.below()).getBlock());
        if (Math.abs(center.getX() - masterPos.getX()) > radius + depositRadius
                || Math.abs(center.getZ() - masterPos.getZ()) > radius + depositRadius) {
            return;
        }
        int bottom = center.getY() - depositRadius - masterPos.getY();
        if (bottom > -1) return;
        int top = Math.min(-1, center.getY() + depositRadius - masterPos.getY());
        int cursor = lastPos == null ? -1 : lastPos.getY();
        if (cursor <= top) {
            lastPos = new BlockPos(-radius, top, -radius);
        }
    }

    private static int clamp(int value, int lo, int hi) {
        return Math.min(hi, Math.max(lo, value));
    }

    private static int getRadius(Block foundation) {
        if (foundation == Blocks.IRON_BLOCK) return 7;
        if (foundation == Blocks.GOLD_BLOCK) return 15;
        if (foundation == Blocks.DIAMOND_BLOCK) return 31;
        if (foundation == Blocks.NETHERITE_BLOCK) return 63;
        if (foundation == NVBlocks.HELLFORGED_BLOCK.block().get()) return 127;
        if (foundation == NVBlocks.SANGUINE_DEUS_BLOCK.block().get()) return MAX_QUARRY_RADIUS;
        return 3;
    }



    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        if (tag.contains("lastPosX")) {
            lastPos = new BlockPos(tag.getInt("lastPosX"), tag.getInt("lastPosY"), tag.getInt("lastPosZ"));
        }
        lastRadius = tag.contains("lastRadius") ? tag.getInt("lastRadius") : -1;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        if (lastPos != null) {
            tag.putInt("lastPosX", lastPos.getX());
            tag.putInt("lastPosY", lastPos.getY());
            tag.putInt("lastPosZ", lastPos.getZ());
        }
        tag.putInt("lastRadius", lastRadius);
    }

    @Override
    public Component[] provideInformationOfRitualToPlayer(Player player) {
        return new Component[]{
                Component.translatable(getTranslationKey() + ".info"),
                Component.translatable(getTranslationKey() + ".spiritus.invictus"),
                Component.translatable(getTranslationKey() + ".spiritus.ruina")
        };
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 1, EnumRuneType.EARTH);
        addCornerRunes(components, 2, 1, EnumRuneType.AIR);
        addParallelRunes(components, 2, 2, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualMagnetism();
    }
}
