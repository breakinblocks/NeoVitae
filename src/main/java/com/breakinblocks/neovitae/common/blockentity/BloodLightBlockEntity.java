package com.breakinblocks.neovitae.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class BloodLightBlockEntity extends BaseBlockEntity {

    private DyeColor color = DyeColor.RED;

    public BloodLightBlockEntity(BlockPos pos, BlockState blockState) {
        super(NVTiles.BLOOD_LIGHT.get(), pos, blockState);
    }

    public DyeColor getColor() {
        return color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Color", color.getId());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Color")) {
            color = DyeColor.byId(tag.getInt("Color"));
        }
    }
}
