// Derived from Blood Magic (https://github.com/WayofTime/BloodMagic), licensed under CC BY 4.0
// SPDX-FileCopyrightText: 2014-2026 WayofTime <https://github.com/WayofTime>
// SPDX-FileCopyrightText: 2024-2026 Saereth <https://github.com/breakinblocks/NeoVitae>
// SPDX-License-Identifier: CC-BY-4.0 AND MIT

package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.spiritus.SpiritusState;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.common.tag.NVTags;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.Utils;
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

    private BlockPos lastPos;

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
        boolean voidFiller = will.getRuina() >= CORROSIVE_DRAIN;
        boolean backfill = !voidFiller && will.hasInvictus() && will.getInvictus() >= STEADFAST_DRAIN;

        BlockPos chestPos = RitualHelper.firstPositionInRange(ctx.master(), this, CHEST_RANGE, masterPos).orElse(masterPos.above());
        ResourceHandler<ItemResource> container = world.getCapability(Capabilities.Item.BLOCK, chestPos, null);
        BlockPos foundationPos = masterPos.below();
        int radius = getRadius(world.getBlockState(foundationPos).getBlock());
        int minRelY = world.getMinY() - masterPos.getY();
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
                            || fillerVoided >= MAX_FILLER_PER_REFRESH
                            || evLeft < Math.max(oreCost, FILLER_COST)) {
                        finished = false;
                        break scan;
                    }
                    visited++;

                    BlockPos srcPos = masterPos.offset(i, j, k);
                    BlockState state = world.getBlockState(srcPos);
                    if (state.isAir()) {
                        k++;
                        continue;
                    }
                    checks++;

                    if (state.is(Tags.Blocks.ORES)) {
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

        if (fillerVoided > 0 && world.getRandom().nextFloat() < CORROSIVE_DRAIN_CHANCE) {
            RitualHelper.drainSpiritus(will, world, masterPos, 0, CORROSIVE_DRAIN, 0, 0, 0);
        }
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
                            ResourceHandler<ItemResource> container, BlockPos masterPos,
                            boolean backfill, SpiritusState will) {
        if (container != null && usesFortune(ctx.master()) && world instanceof ServerLevel serverLevel) {
            return mineWithFortune(ctx, serverLevel, srcPos, state, container, masterPos, backfill, will);
        }
        if (container != null) {
            ItemStack oreStack = new ItemStack(state.getBlock().asItem());
            if (!oreStack.isEmpty() && oreStack.getItem() != Items.AIR) {
                ItemStack remainder = Utils.insertItemStacked(container, oreStack, false);
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
                                    ResourceHandler<ItemResource> container, BlockPos masterPos,
                                    boolean backfill, SpiritusState will) {
        ItemStack tool = RitualHelper.createMiningTool(level, true, false);
        List<ItemStack> drops = Block.getDrops(state, level, srcPos, level.getBlockEntity(srcPos), null, tool);
        for (ItemStack drop : drops) {
            if (!Utils.insertItemStacked(container, drop.copy(), true).isEmpty()) {
                return false;
            }
        }
        for (ItemStack drop : drops) {
            Utils.insertItemStacked(container, drop, false);
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
        if (backfill && will.getInvictus() >= STEADFAST_DRAIN) {
            world.setBlock(srcPos, fillerFor(world, srcPos), Block.UPDATE_ALL);
            RitualHelper.drainSpiritus(will, world, masterPos, 0, 0, 0, 0, STEADFAST_DRAIN);
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

    private BlockPos findFreePlacementSlot(RitualContext ctx, BlockPos masterPos) {
        for (BlockPos offset : RitualHelper.getRangePositions(ctx.master(), this, PLACEMENT_RANGE, masterPos)) {
            if (ctx.level().isEmptyBlock(offset)) {
                return offset.immutable();
            }
        }
        return null;
    }

    private static int clamp(int value, int lo, int hi) {
        return Math.min(hi, Math.max(lo, value));
    }

    private static int getRadius(Block foundation) {
        if (foundation == Blocks.IRON_BLOCK) return 7;
        if (foundation == Blocks.GOLD_BLOCK) return 15;
        if (foundation == Blocks.DIAMOND_BLOCK) return 31;
        if (foundation == Blocks.NETHERITE_BLOCK) return 63;
        return 3;
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        tag.getInt("lastPosX").ifPresent(x -> {
            int y = tag.getIntOr("lastPosY", 0);
            int z = tag.getIntOr("lastPosZ", 0);
            lastPos = new BlockPos(x, y, z);
        });
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        if (lastPos != null) {
            tag.putInt("lastPosX", lastPos.getX());
            tag.putInt("lastPosY", lastPos.getY());
            tag.putInt("lastPosZ", lastPos.getZ());
        }
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
