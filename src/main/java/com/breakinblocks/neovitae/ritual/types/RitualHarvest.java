package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.api.stream.StreamPresets;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.ritual.harvest.HarvestRegistry;
import com.breakinblocks.neovitae.ritual.harvest.IHarvestHandler;
import com.breakinblocks.neovitae.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual that automatically harvests and replants crops using registered harvest handlers.
 */
public class RitualHarvest extends Ritual {

    public static final String HARVEST_RANGE = "harvestRange";
    public static final String CHEST_RANGE = "chestRange";

    public RitualHarvest() {
        super("harvest", 0, 20000, "ritual." + NeoVitae.MODID + ".harvest");
        addBlockRange(HARVEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, -8, -5), 11, 17, 11));
        addBlockRange(CHEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 1, 1, 1));

        setMaximumVolumeAndDistanceOfRange(HARVEST_RANGE, 4000, 15, 16);
        setMaximumVolumeAndDistanceOfRange(CHEST_RANGE, 1, 5, 5);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        int maxHarvests = ctx.maxOperations(getRefreshCost());
        int totalHarvests = 0;

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, HARVEST_RANGE, ctx.masterPos());
        List<IHarvestHandler> handlers = HarvestRegistry.getHarvestHandlers();
        UUID owner = ctx.master().getOwner();

        RitualHelper.ChestOutput chest = RitualHelper.resolveChestOutput(ctx, this, CHEST_RANGE);
        BlockEntity chestTile = chest.tile();
        ResourceHandler<ItemResource> outputHandler = chestTile != null ? Utils.getInventory(chestTile, Direction.DOWN) : null;

        if (outputHandler != null && !chest.hasFreeSlot()) {
            return;
        }

        boolean chestFull = false;
        BlockPos masterPos = ctx.masterPos();
        for (BlockPos pos : positions) {
            if (totalHarvests >= maxHarvests || chestFull) break;

            BlockState state = ctx.level().getBlockState(pos);
            if (state.isAir()) continue;

            for (IHarvestHandler handler : handlers) {
                if (handler.test(ctx.level(), pos, state)) {
                    List<ItemStack> drops = new ArrayList<>();

                    if (handler.harvest(ctx.level(), pos, state, drops, owner)) {
                        for (ItemStack drop : drops) {
                            if (drop.isEmpty()) continue;
                            if (outputHandler != null) {
                                ItemStack remainder = Utils.insertItemStacked(outputHandler, drop, false);
                                if (!remainder.isEmpty()) {
                                    Block.popResource(ctx.level(), pos, remainder);
                                    chestFull = true;
                                }
                            } else {
                                Block.popResource(ctx.level(), pos, drop);
                            }
                        }
                        totalHarvests++;
                        RitualHelper.chanceStream(ctx.level(), 10, () ->
                                StreamPresets.lifePulse(pos, masterPos).color(0x44CC33).build()
                                        .sendToNearby(ctx.serverLevel(), masterPos, 32));
                        break;
                    }
                }
            }
        }

        ctx.syphon(getRefreshCost() * totalHarvests);
    }


    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addParallelRunes(components, 1, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
    }

    @Override
    public SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualHarvest();
    }
}
