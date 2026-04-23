package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.NVSounds;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;
import com.breakinblocks.neovitae.common.particle.NVParticles;

public class BloodTankBlockEntity extends BaseBlockEntity {
    public static final int[] CAPACITIES = {16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288};

    private int tier;
    private int previousFluidAmount = 0;
    private final Tank tank = new Tank();

    private class Tank extends FluidStacksResourceHandler {
        Tank() {
            super(1, FluidType.BUCKET_VOLUME);
        }

        void setTankCapacity(int value) {
            this.capacity = value;
        }

        @Override
        protected void onContentsChanged(int index, FluidStack previousContents) {
            setChanged();
            if (level == null || level.isClientSide()) return;
            int currentAmount = getAmountAsInt(0);
            if (currentAmount > previousFluidAmount) {
                level.playSound(null, getBlockPos(), NVSounds.BLOOD_TANK_FILL.get(), SoundSource.BLOCKS, 0.4f, 1.0f);
                ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0.01);
            } else if (currentAmount < previousFluidAmount) {
                level.playSound(null, getBlockPos(), NVSounds.BLOOD_TANK_DRAIN.get(), SoundSource.BLOCKS, 0.4f, 1.0f);
                ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0.01);
            }
            if (currentAmount > capacity * 0.9) {
                ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), 0x990011), getBlockPos().getX() + 0.5, getBlockPos().getY() + 1.0, getBlockPos().getZ() + 0.5, 1, 0.2, 0.0, 0.2, 0);
            }
            previousFluidAmount = currentAmount;
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public BloodTankBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.BLOOD_TANK_TYPE.get(), pos, state);
        updateCapacity();
    }

    private void updateCapacity() {
        int slot = Math.max(0, Math.min(CAPACITIES.length - 1, tier - 1));
        tank.setTankCapacity(FluidType.BUCKET_VOLUME * CAPACITIES[slot]);
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        tier = tag.getIntOr("tier", 0);
        updateCapacity();
        tank.deserialize(tag);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putInt("tier", tier);
        tank.serialize(tag);
    }

    public static @Nullable ResourceHandler<FluidResource> getFluidHandler(BloodTankBlockEntity tile, @Nullable Direction direction) {
        return tile.tank;
    }

    public FluidStack getFluidContained() {
        FluidResource r = tank.getResource(0);
        if (r.isEmpty()) return FluidStack.EMPTY;
        return r.toStack(tank.getAmountAsInt(0));
    }

    public int getCapacity() {
        return tank.getCapacityAsInt(0, FluidResource.EMPTY);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);
        this.tier = componentInput.getOrDefault(NVDataComponents.CONTAINER_TIER, 0);
        FluidStack stack = componentInput.getOrDefault(NVDataComponents.FLUID_CONTENT, SimpleFluidContent.EMPTY).copy();
        updateCapacity();
        tank.set(0, FluidResource.of(stack), stack.getAmount());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(NVDataComponents.CONTAINER_TIER, this.tier);
        components.set(NVDataComponents.FLUID_CONTENT, SimpleFluidContent.copyOf(getFluidContained()));
    }
}
