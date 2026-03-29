package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;
import com.breakinblocks.neovitae.NeoVitae;
import com.breakinblocks.neovitae.client.particle.ColoredParticleOptions;
import com.breakinblocks.neovitae.common.particle.NVParticles;
import com.breakinblocks.neovitae.common.datacomponent.NVDataComponents;

public class BloodTankBlockEntity extends BaseBlockEntity {
    private int tier;
    private int previousFluidAmount = 0;
    public static final int[] CAPACITIES = {16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288};
    private final FluidTank tank = new FluidTank(FluidType.BUCKET_VOLUME) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if (level != null && !level.isClientSide) {
                int currentAmount = getFluidAmount();
                if (currentAmount > previousFluidAmount) {
                    level.playSound(null, getBlockPos(), com.breakinblocks.neovitae.common.NVSounds.BLOOD_TANK_FILL.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.4f, 1.0f);
                    ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0.01);
                } else if (currentAmount < previousFluidAmount) {
                    level.playSound(null, getBlockPos(), com.breakinblocks.neovitae.common.NVSounds.BLOOD_TANK_DRAIN.get(), net.minecraft.sounds.SoundSource.BLOCKS, 0.4f, 1.0f);
                    ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_FLAME.get(), 0xAA0000), getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0.01);
                }
                // Blood drip when tank is nearly full (>90%)
                if (currentAmount > getCapacity() * 0.9) {
                    ((ServerLevel) level).sendParticles(new ColoredParticleOptions(NVParticles.BLOOD_DRIP.get(), 0x990011), getBlockPos().getX() + 0.5, getBlockPos().getY() + 1.0, getBlockPos().getZ() + 0.5, 1, 0.2, 0.0, 0.2, 0);
                }
                previousFluidAmount = currentAmount;
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    };

    public BloodTankBlockEntity(BlockPos pos, BlockState state) {
        super(NVTiles.BLOOD_TANK_TYPE.get(), pos, state);
    }

    private void updateCapacity() {
        this.tank.setCapacity(FluidType.BUCKET_VOLUME * CAPACITIES[tier -1]);
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        CompoundTag tankTag = tag.getCompound("tank");
        tank.readFromNBT(registries, tankTag);
        tier = tag.getInt("tier");
        updateCapacity();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        CompoundTag tankTag = new CompoundTag();
        tank.writeToNBT(registries, tankTag);
        tag.put("tank", tankTag);
        tag.putInt("tier", tier);
    }

    public static @Nullable IFluidHandler getFluidHandler(BloodTankBlockEntity tile, @Nullable Direction direction) {
        return tile.tank;
    }

    public FluidStack getFluidContained() {
        return this.tank.getFluid();
    }

    public int getCapacity() {
        return this.tank.getCapacity();
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.tier = componentInput.getOrDefault(NVDataComponents.CONTAINER_TIER, 0);
        FluidStack stack = componentInput.getOrDefault(NVDataComponents.FLUID_CONTENT, SimpleFluidContent.EMPTY).copy();
        this.tank.setFluid(stack);
        updateCapacity();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(NVDataComponents.CONTAINER_TIER, this.tier);
        components.set(NVDataComponents.FLUID_CONTENT, SimpleFluidContent.copyOf(this.tank.getFluid()));
    }
}
