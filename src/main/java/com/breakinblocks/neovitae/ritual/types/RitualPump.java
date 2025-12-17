package com.breakinblocks.neovitae.ritual.types;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.api.ritual.AreaDescriptor;
import com.breakinblocks.neovitae.ritual.*;
import com.breakinblocks.neovitae.ritual.RitualHelper.RitualContext;
import com.breakinblocks.neovitae.util.helper.BlockProtectionHelper;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Ritual that pumps fluids from an area into an adjacent tank.
 */
public class RitualPump extends Ritual {

    public static final String PUMP_RANGE = "pumpRange";

    public RitualPump() {
        super("pump", 0, 2500, "ritual." + NeoVitae.MODID + ".pump");
        addBlockRange(PUMP_RANGE, new AreaDescriptor.Rectangle(new BlockPos(-10, -5, -10), 21, 6, 21));
        setMaximumVolumeAndDistanceOfRange(PUMP_RANGE, 5000, 20, 20);
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        RitualContext ctx = RitualHelper.createContext(masterRitualStone, getRefreshCost());
        if (ctx == null) return;

        // Find a fluid handler above the MRS
        BlockEntity tankBe = ctx.level().getBlockEntity(ctx.masterPos().above());
        if (tankBe == null) return;

        IFluidHandler tank = ctx.level().getCapability(Capabilities.FluidHandler.BLOCK, ctx.masterPos().above(), null);
        if (tank == null) return;

        List<BlockPos> positions = RitualHelper.getRangePositions(ctx.master(), this, PUMP_RANGE, ctx.masterPos());
        UUID owner = ctx.master().getOwner();
        int fluidsPumped = 0;
        int maxFluids = ctx.maxOperations(getRefreshCost());

        for (BlockPos pos : positions) {
            if (fluidsPumped >= maxFluids) break;

            BlockState state = ctx.level().getBlockState(pos);
            FluidState fluidState = state.getFluidState();

            if (!fluidState.isEmpty() && fluidState.isSource()) {
                // Check protection before removing fluid
                if (!BlockProtectionHelper.canBreakBlock(ctx.level(), pos, owner)) {
                    continue;
                }

                FluidStack fluidStack = new FluidStack(fluidState.getType(), 1000);

                // Try to insert into tank
                int filled = tank.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
                if (filled == 1000) {
                    tank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);

                    // Remove the fluid source
                    if (state.getBlock() instanceof BucketPickup bucketPickup) {
                        bucketPickup.pickupBlock(null, ctx.level(), pos, state);
                    } else {
                        ctx.level().setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                    fluidsPumped++;
                }
            }
        }

        ctx.syphon(getRefreshCost() * fluidsPumped);
    }

    @Override
    public int getRefreshTime() {
        return 20;
    }

    @Override
    public int getRefreshCost() {
        return 50;
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        addCornerRunes(components, 1, 0, EnumRuneType.WATER);
        addParallelRunes(components, 2, 0, EnumRuneType.WATER);
        addCornerRunes(components, 2, 0, EnumRuneType.EARTH);
        addRune(components, 3, 0, 0, EnumRuneType.WATER);
        addRune(components, -3, 0, 0, EnumRuneType.WATER);
        addRune(components, 0, 0, 3, EnumRuneType.WATER);
        addRune(components, 0, 0, -3, EnumRuneType.WATER);
        addCornerRunes(components, 3, 0, EnumRuneType.FIRE);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualPump();
    }
}
