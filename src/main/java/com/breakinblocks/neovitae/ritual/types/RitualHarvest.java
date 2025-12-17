package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.ritual.harvest.HarvestRegistry;
import com.breakinblocks.neovitae.ritual.harvest.IHarvestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual that automatically harvests and replants crops using registered harvest handlers.
 */
public class RitualHarvest extends Ritual {

    public static final String HARVEST_RANGE = "harvestRange";

    public RitualHarvest() {
        super("harvest", 0, 20000, "ritual." + NeoVitae.MODID + ".harvest");
        addBlockRange(HARVEST_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-5, 0, -5), 11, 5, 11));
        setMaximumVolumeAndDistanceOfRange(HARVEST_RANGE, 1000, 15, 15);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        if (!(ctx.level() instanceof ServerLevel)) return;

        int maxHarvests = ctx.maxOperations(getRefreshCost());
        int totalHarvests = 0;

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, HARVEST_RANGE, ctx.masterPos());
        List<IHarvestHandler> handlers = HarvestRegistry.getHarvestHandlers();
        UUID owner = ctx.master().getOwner();

        for (BlockPos pos : positions) {
            if (totalHarvests >= maxHarvests) break;

            BlockState state = ctx.level().getBlockState(pos);

            // Try each registered handler
            for (IHarvestHandler handler : handlers) {
                if (handler.test(ctx.level(), pos, state)) {
                    List<ItemStack> drops = new ArrayList<>();

                    if (handler.harvest(ctx.level(), pos, state, drops, owner)) {
                        // Pop the drops into the world
                        for (ItemStack drop : drops) {
                            if (!drop.isEmpty()) {
                                Block.popResource(ctx.level(), pos, drop);
                            }
                        }
                        totalHarvests++;
                        break; // Only harvest once per position
                    }
                }
            }
        }

        ctx.syphon(getRefreshCost() * totalHarvests);
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 20;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addParallelRunes(components, 1, 0, EnumRuneType.EARTH);
        addCornerRunes(components, 1, 0, EnumRuneType.EARTH);
        addParallelRunes(components, 2, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualHarvest();
    }
}
