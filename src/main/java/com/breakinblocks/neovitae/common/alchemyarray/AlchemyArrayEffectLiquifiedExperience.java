package com.breakinblocks.neovitae.common.alchemyarray;

import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.blockentity.AlchemyArrayBlockEntity;
import com.breakinblocks.neovitae.common.fluid.NVFluids;
import com.breakinblocks.neovitae.common.item.ExperienceTomeItem;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class AlchemyArrayEffectLiquifiedExperience extends AlchemyArrayEffect {

    public static final TagKey<Fluid> EXPERIENCE = TagKey.create(Registries.FLUID,
            ResourceLocation.fromNamespaceAndPath("c", "experience"));

    private static final int INTERVAL = 20;

    private static String resolvedFrom;
    private static Fluid resolved;

    @Override
    public boolean update(AlchemyArrayBlockEntity tile, int ticksActive) {
        Level level = tile.getLevel();
        if (level == null || level.isClientSide() || ticksActive % INTERVAL != 0) {
            return false;
        }

        Fluid experience = experienceFluid();
        if (experience == null) {
            return false;
        }

        BlockPos arrayPos = tile.getBlockPos();
        BlockPos containerPos = arrayPos.below();
        IItemHandler container = level.getCapability(Capabilities.ItemHandler.BLOCK, containerPos, null);
        if (container == null) {
            return false;
        }

        IFluidHandler tank = findTank(level, arrayPos, containerPos);
        if (tank == null) {
            return false;
        }

        int perPoint = NeoVitae.SERVER_CONFIG.LIQUIFIED_EXPERIENCE_MB_PER_POINT.get();
        int budget = NeoVitae.SERVER_CONFIG.LIQUIFIED_EXPERIENCE_POINTS_PER_OPERATION.get();
        if (perPoint <= 0 || budget <= 0) {
            return false;
        }

        boolean reversed = level.hasNeighborSignal(arrayPos);
        int moved = reversed
                ? fillTomes(container, tank, experience, perPoint, budget)
                : drainTomes(container, tank, experience, perPoint, budget);

        if (moved > 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_GLOW.get(), reversed ? 0x66DD33 : 0x88FF44),
                    arrayPos.getX() + 0.5, arrayPos.getY() + 0.3, arrayPos.getZ() + 0.5,
                    4, 0.25, 0.1, 0.25, 0.01);
        }
        return false;
    }

    private static int drainTomes(IItemHandler container, IFluidHandler tank, Fluid experience, int perPoint, int budget) {
        int moved = 0;
        for (int slot = 0; slot < container.getSlots() && budget > 0; slot++) {
            ItemStack stack = container.getStackInSlot(slot);
            if (!(stack.getItem() instanceof ExperienceTomeItem)) {
                continue;
            }
            int stored = ExperienceTomeItem.getStoredXp(stack);
            if (stored <= 0) {
                continue;
            }
            int points = Math.min(stored, budget);
            int accepted = tank.fill(new FluidStack(experience, points * perPoint), IFluidHandler.FluidAction.SIMULATE);
            points = accepted / perPoint;
            if (points <= 0) {
                continue;
            }
            tank.fill(new FluidStack(experience, points * perPoint), IFluidHandler.FluidAction.EXECUTE);
            ExperienceTomeItem.addXpToTome(stack, -points);
            budget -= points;
            moved += points;
        }
        return moved;
    }

    private static int fillTomes(IItemHandler container, IFluidHandler tank, Fluid experience, int perPoint, int budget) {
        int moved = 0;
        for (int slot = 0; slot < container.getSlots() && budget > 0; slot++) {
            ItemStack stack = container.getStackInSlot(slot);
            if (!(stack.getItem() instanceof ExperienceTomeItem)) {
                continue;
            }
            FluidStack available = tank.drain(new FluidStack(experience, budget * perPoint), IFluidHandler.FluidAction.SIMULATE);
            if (available.isEmpty() || !available.is(experience)) {
                break;
            }
            int points = available.getAmount() / perPoint;
            if (points <= 0) {
                break;
            }
            tank.drain(new FluidStack(experience, points * perPoint), IFluidHandler.FluidAction.EXECUTE);
            ExperienceTomeItem.addXpToTome(stack, points);
            budget -= points;
            moved += points;
        }
        return moved;
    }

    private static IFluidHandler findTank(Level level, BlockPos arrayPos, BlockPos containerPos) {
        for (Direction direction : Direction.values()) {
            BlockPos side = arrayPos.relative(direction);
            if (side.equals(containerPos)) {
                continue;
            }
            IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK, side, direction.getOpposite());
            if (handler != null) {
                return handler;
            }
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            IFluidHandler handler = level.getCapability(Capabilities.FluidHandler.BLOCK,
                    containerPos.relative(direction), direction.getOpposite());
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    /**
     * Neo Vitae supplies its own experience fluid, so this always resolves. Packs that would
     * rather use another mod's fluid name it in the server config.
     */
    private static Fluid experienceFluid() {
        String configured = NeoVitae.SERVER_CONFIG.LIQUIFIED_EXPERIENCE_FLUID.get();
        if (!java.util.Objects.equals(configured, resolvedFrom)) {
            resolvedFrom = configured;
            resolved = null;
            if (configured != null && !configured.isBlank()) {
                ResourceLocation id = ResourceLocation.tryParse(configured.trim());
                Fluid fluid = id == null ? null : BuiltInRegistries.FLUID.get(id);
                if (fluid != null && fluid != Fluids.EMPTY) {
                    resolved = fluid;
                } else {
                    NeoVitae.LOGGER.warn("Unknown liquified_experience preferred_fluid '{}', using Neo Vitae's own", configured);
                }
            }
            if (resolved == null) {
                resolved = NVFluids.LIQUIFIED_EXPERIENCE_SOURCE.get();
            }
        }
        return resolved;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
    }

    @Override
    public AlchemyArrayEffect getNewCopy() {
        return new AlchemyArrayEffectLiquifiedExperience();
    }
}
