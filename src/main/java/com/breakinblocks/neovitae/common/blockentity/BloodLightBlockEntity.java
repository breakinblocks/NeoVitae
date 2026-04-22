package com.breakinblocks.neovitae.common.blockentity;


import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class BloodLightBlockEntity extends BaseBlockEntity {

    private DyeColor color = DyeColor.RED;
    private boolean redstoneControlled = false;

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

    public boolean isRedstoneControlled() {
        return redstoneControlled;
    }

    public void setRedstoneControlled(boolean redstoneControlled) {
        this.redstoneControlled = redstoneControlled;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        tag.putInt("Color", color.getId());
        tag.putBoolean("RedstoneControlled", redstoneControlled);
    }

    @Override
    protected void loadAdditional(ValueInput tag) {
        super.loadAdditional(tag);
        color = DyeColor.byId(tag.getIntOr("Color", color.getId()));
        redstoneControlled = tag.getBooleanOr("RedstoneControlled", redstoneControlled);
    }
}
