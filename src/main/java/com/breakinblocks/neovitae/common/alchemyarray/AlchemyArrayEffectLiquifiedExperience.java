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
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class AlchemyArrayEffectLiquifiedExperience extends AlchemyArrayEffect {

    public static final TagKey<Fluid> EXPERIENCE = TagKey.create(Registries.FLUID,
            Identifier.fromNamespaceAndPath("c", "experience"));

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
        ResourceHandler<ItemResource> container = level.getCapability(Capabilities.Item.BLOCK, containerPos, null);
        if (container == null) {
            return false;
        }

        ResourceHandler<FluidResource> tank = findTank(level, arrayPos, containerPos);
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

    private static int drainTomes(ResourceHandler<ItemResource> container, ResourceHandler<FluidResource> tank,
                                  Fluid experience, int perPoint, int budget) {
        FluidResource resource = FluidResource.of(experience);
        int moved = 0;
        for (int slot = 0; slot < container.size() && budget > 0; slot++) {
            ItemStack stack = container.getResource(slot).toStack(container.getAmountAsInt(slot));
            if (!(stack.getItem() instanceof ExperienceTomeItem)) {
                continue;
            }
            int stored = ExperienceTomeItem.getStoredXp(stack);
            if (stored <= 0) {
                continue;
            }
            int points = Math.min(stored, budget);
            int accepted;
            try (Transaction probe = Transaction.openRoot()) {
                accepted = insertAll(tank, resource, points * perPoint, probe);
            }
            points = accepted / perPoint;
            if (points <= 0) {
                continue;
            }
            try (Transaction tx = Transaction.openRoot()) {
                if (insertAll(tank, resource, points * perPoint, tx) < points * perPoint) {
                    continue;
                }
                tx.commit();
            }
            ExperienceTomeItem.addXpToTome(stack, -points);
            writeBack(container, slot, stack);
            budget -= points;
            moved += points;
        }
        return moved;
    }

    private static int fillTomes(ResourceHandler<ItemResource> container, ResourceHandler<FluidResource> tank,
                                 Fluid experience, int perPoint, int budget) {
        int moved = 0;
        for (int slot = 0; slot < container.size() && budget > 0; slot++) {
            ItemStack stack = container.getResource(slot).toStack(container.getAmountAsInt(slot));
            if (!(stack.getItem() instanceof ExperienceTomeItem)) {
                continue;
            }
            int drained = 0;
            try (Transaction tx = Transaction.openRoot()) {
                for (int tankIndex = 0; tankIndex < tank.size() && drained < budget * perPoint; tankIndex++) {
                    FluidResource held = tank.getResource(tankIndex);
                    if (held.isEmpty() || held.getFluid() != experience) continue;
                    drained += tank.extract(tankIndex, held, budget * perPoint - drained, tx);
                }
                int points = drained / perPoint;
                if (points <= 0) {
                    break;
                }
                tx.commit();
                ExperienceTomeItem.addXpToTome(stack, points);
                writeBack(container, slot, stack);
                budget -= points;
                moved += points;
            }
        }
        return moved;
    }

    private static int insertAll(ResourceHandler<FluidResource> tank, FluidResource resource, int amount, Transaction tx) {
        int inserted = 0;
        for (int i = 0; i < tank.size() && inserted < amount; i++) {
            inserted += tank.insert(i, resource, amount - inserted, tx);
        }
        return inserted;
    }

    /** Tome contents live in data components, so the edited stack has to be put back in the slot. */
    private static void writeBack(ResourceHandler<ItemResource> container, int slot, ItemStack stack) {
        try (Transaction tx = Transaction.openRoot()) {
            ItemResource current = container.getResource(slot);
            int count = container.getAmountAsInt(slot);
            if (container.extract(slot, current, count, tx) == count
                    && container.insert(slot, ItemResource.of(stack), count, tx) == count) {
                tx.commit();
            }
        }
    }

    private static ResourceHandler<FluidResource> findTank(Level level, BlockPos arrayPos, BlockPos containerPos) {
        for (Direction direction : Direction.values()) {
            BlockPos side = arrayPos.relative(direction);
            if (side.equals(containerPos)) {
                continue;
            }
            ResourceHandler<FluidResource> handler = level.getCapability(Capabilities.Fluid.BLOCK, side, direction.getOpposite());
            if (handler != null) {
                return handler;
            }
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            ResourceHandler<FluidResource> handler = level.getCapability(Capabilities.Fluid.BLOCK,
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
                Identifier id = Identifier.tryParse(configured.trim());
                Fluid fluid = id == null ? null : BuiltInRegistries.FLUID.getValue(id);
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
